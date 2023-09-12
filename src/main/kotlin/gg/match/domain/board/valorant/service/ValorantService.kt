package gg.match.domain.board.valorant.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.valorant.dto.AgentByMatchDTO
import gg.match.domain.board.valorant.dto.HistoryDTO
import gg.match.domain.board.valorant.dto.ValorantUserTokenDTO
import gg.match.domain.board.valorant.entity.Agent
import gg.match.domain.board.valorant.entity.AgentByMatch
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
    val parser = JSONParser()

    fun getValorantUser(code: String): JsonNode{
        try {
            val rsoReturnJson = requestRiotAccessToken(code) ?: throw BusinessException(ErrorCode.BAD_REQUEST)
            val valorantUser = getValorantUserData(rsoReturnJson)
            val puuid = valorantUser["puuid"].asText()
            val agentName = valorantUser["gameName"] as String + "#" + valorantUser["tagLine"] as String
            val agent: Agent = objectMapper.readValue(rsoReturnJson.toString(), ValorantUserTokenDTO::class.java)
                .toEntity(puuid, agentName)
            agentRepository.save(agent)
            return valorantUser
        } catch(e: Exception){
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
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

        println(matchList)

    }
}