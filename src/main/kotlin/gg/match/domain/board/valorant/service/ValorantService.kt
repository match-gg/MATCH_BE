package gg.match.domain.board.valorant.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.valorant.dto.ValorantUserTokenDTO
import gg.match.domain.board.valorant.entity.Agent
import gg.match.domain.board.valorant.entity.ValorantGameModes
import gg.match.domain.board.valorant.repository.AgentByMatchRepository
import gg.match.domain.board.valorant.repository.AgentRepository
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
@Transactional(readOnly = true)
class ValorantService (
    @Value("\${valorant.client-id}") private val valorantClientId: String,
    @Value("\${valorant.client-secret}") private val valorantClientSecret: String,
    @Value("\${valorant.callback-uri}") private val valorantCallbackUri: String,
    @Value("\${valorant.mykey}") private val mykey: String,
    private val agentRepository: AgentRepository,
    private val agentByMatchRepository: AgentByMatchRepository,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
){
    private val tokenUrl: String = "https://auth.riotgames.com/token"
    private val infoUrl: String = "https://asia.api.riotgames.com/riot/account/v1/accounts/me"
    private val matchListUrl: String = "https://kr.api.riotgames.com/val/match/v1/matchlists/by-puuid"
    private val matchUrl: String = "https://kr.api.riotgames.com/val/match/v1/matches"
    val parser = JSONParser()

    fun getValorantUser(code: String): JsonNode{
        val rsoReturnJson = requestRiotAccessToken(code) ?: throw BusinessException(ErrorCode.BAD_REQUEST)
        val valorantUser = getValorantUserData(rsoReturnJson)
        val puuid = valorantUser["puuid"].asText()
        val agentName = "${valorantUser["gameName"].asText()}#${valorantUser["tagLine"].asText()}"

        if(agentRepository.existsByAgentName(agentName)){
            agentRepository.findByAgentName(agentName)?.let { agentRepository.delete(it) }
        }

        val agent: Agent = objectMapper.readValue(rsoReturnJson.toString(), ValorantUserTokenDTO::class.java)
            .toEntity(puuid, agentName)
        agentRepository.save(agent)
        return valorantUser
    }

    private fun requestRiotAccessToken(code: String): JsonNode? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val params = LinkedMultiValueMap<String, String>()
        params.add("grant_type", "authorization_code")
        params.add("code", code)
        params.add("redirect_uri", valorantCallbackUri)
        params.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
        params.add("client_assertion", valorantClientSecret)

        val request = HttpEntity(params, httpHeaders)
        val response: ResponseEntity<String> = try{
            restTemplate.postForEntity(tokenUrl, request, String::class.java)
        } catch(e: HttpClientErrorException){
            e.printStackTrace()
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        return try {
            objectMapper.readTree(response.body)
        } catch (e: JsonProcessingException) {
            throw Exception("반환 에러")
        }
    }

    private fun getValorantUserData(rsoReturnJson: JsonNode): JsonNode{
        val accessToken = rsoReturnJson.get("access_token").asText()

        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        httpHeaders.set("Authorization", "Bearer $accessToken")

        val request = HttpEntity(null, httpHeaders)
        val response: ResponseEntity<String> = try{
            restTemplate.exchange(infoUrl, HttpMethod.GET, request, String::class.java)
        } catch(e: HttpClientErrorException){
            e.printStackTrace()
            if(e.statusCode == HttpStatus.NOT_FOUND){
                throw BusinessException(ErrorCode.USER_NOT_FOUND)
            }
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        return try {
            objectMapper.readTree(response.body)
        } catch (e: JsonProcessingException) {
            throw Exception("반환 에러")
        }
    }

    fun saveValorantUserData(valorantUserName: String): Any{
        val puuid = agentRepository.findByAgentName(valorantUserName)?.puuid ?: BusinessException(ErrorCode.USER_NOT_FOUND)
        saveValorantMatchHistory(puuid.toString())
        return "good"
    }

    private fun saveValorantMatchHistory(puuid: String) {
        var matchList =  ArrayList<String>()
        val request = HttpGet("$matchListUrl/$puuid?api_key=$mykey")
        val responseMatchList: HttpResponse = HttpClientBuilder.create().build().execute(request)

        val matchHistory = parser.parse(EntityUtils.toString(responseMatchList.entity, "UTF-8")) as JSONObject

//        val jsonString = EntityUtils.toString(responseMatchList.entity, "UTF-8")
//        val gson = Gson()
//        val sample = gson.fromJson(jsonString, HistoryDTO::class.java)

        for(element in matchHistory["history"] as JSONArray){
            val history = element as JSONObject
            matchList.add(history["matchId"].toString())
        }
        for(element in matchList){
            getMatchData(element, puuid)
        }
    }

    private fun getMatchData(matchId: String, puuid: String){
        var damage = 0L
        var leg = 0L
        var body = 0L
        var head = 0L

        val request = HttpGet("$matchUrl/$matchId?api_key=$mykey")
        val responseMatch: HttpResponse = HttpClientBuilder.create().build().execute(request)
        val matchHistory = parser.parse(EntityUtils.toString(responseMatch.entity, "UTF-8")) as JSONObject
        val matchInfo = matchHistory["matchInfo"] as JSONObject
        val gameMode = ValorantGameModes.assetPathToName(matchInfo["gameMode"].toString())
        var rounds = 0
        var damageData: Array<Long>
        val roundResults = matchHistory["roundResults"] as JSONArray

        for(roundResult in roundResults) {
            rounds += 1
            damageData = getRoundResultData(roundResult as JSONObject, puuid)
            damage += damageData[0]
            leg += damageData[1]
            body += damageData[2]
            head += damageData[3]
        }
        val shots = leg+body+head
        val avgDmg = damage / rounds
        println("head=$head shots=$shots avgDmg = $avgDmg")
    }

    private fun getRoundResultData(roundResult: JSONObject, puuid: String): Array<Long> {
        val playerStats = roundResult["playerStats"] as JSONArray
        var damageDTO: JSONArray
        var damage = 0L
        var leg = 0L
        var body = 0L
        var head = 0L
        for(playerStat in playerStats){
            playerStat as JSONObject
            if(playerStat["puuid"] != puuid){
                continue
            }
            damageDTO = playerStat["damage"] as JSONArray
            for(damageInfo in damageDTO){
                damageInfo as JSONObject
                damage += damageInfo["damage"] as Long
                leg += damageInfo["legshots"] as Long
                body += damageInfo["bodyshots"] as Long
                head += damageInfo["headshots"] as Long
            }
        }
        println("damage=$damage, leg=$leg, body=$body, head=$head")
        return arrayOf(damage, leg, body, head)
    }
}