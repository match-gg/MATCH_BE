package gg.match.domain.board.valorant.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.valorant.dto.AgentByMatchDTO
import gg.match.domain.board.valorant.dto.AgentReadDTO
import gg.match.domain.board.valorant.dto.ValorantUserTokenDTO
import gg.match.domain.board.valorant.entity.Agent
import gg.match.domain.board.valorant.entity.ValorantCharacters
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
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

@Service
@Transactional
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

    fun existByUserNickname(userName: String): Boolean{
        return agentRepository.existsByName(userName)
    }

    fun getValorantUser(code: String): JsonNode{
        val rsoReturnJson = requestRiotAccessToken(code) ?: throw BusinessException(ErrorCode.BAD_REQUEST)
        val valorantUser = getValorantUserData(rsoReturnJson)
        val puuid = valorantUser["puuid"].asText()
        val agentName = "${valorantUser["gameName"].asText()}#${valorantUser["tagLine"].asText()}"
        agentRepository.deleteAllByPuuid(puuid)

        val agent: Agent = objectMapper.readValue(rsoReturnJson.toString(), ValorantUserTokenDTO::class.java)
            .toEntity(puuid, agentName)
        agentRepository.save(agent)
        return valorantUser
    }

    private fun getMostAgent(usingAgentList: MutableList<String>): List<Pair<String, Int>> {
        val agentSet: Set<String>
        val agentMap = mutableMapOf<String, Int>()

        agentSet = HashSet<String>(usingAgentList)
        for(agentName: String in agentSet)
            agentMap[agentName] = Collections.frequency(usingAgentList, agentName)
        return agentMap.toList().sortedByDescending { it.second }
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

    fun saveValorantUserData(valorantUserName: String){
        val puuid = agentRepository.findByNameAndGameMode(valorantUserName, "NONE")?.puuid
        saveValorantMatchHistory(puuid.toString())
    }

    private fun saveValorantMatchHistory(puuid: String) {
        val matchList =  ArrayList<String>()
        val request = HttpGet("$matchListUrl/$puuid?api_key=$mykey")
        val responseMatchList: HttpResponse = HttpClientBuilder.create().build().execute(request)

        val matchHistory = parser.parse(EntityUtils.toString(responseMatchList.entity, "UTF-8")) as JSONObject

        for(element in matchHistory["history"] as JSONArray){
            val history = element as JSONObject
            matchList.add(history["matchId"].toString())
        }

        for(element in matchList){
            if(!agentByMatchRepository.existsByMatchId(element)){
                getMatchData(element, puuid)
            }
        }
        //add user most data in DB
        //get agent info in agent_match table
        val initArray = arrayOf(0L, 0L, 0L, 0L)
        var (avgDmg, tier, kills, deaths) = initArray
        var (wins, losses, heads, shots) = initArray
        var most1Agent: String
        var most2Agent = "poro"
        var most3Agent = "poro"
        val tierList: MutableList<Long> = arrayListOf()
        val usingAgentList = mutableListOf<String>()
        var mostAgents: List<Pair<String, Int>>
        var agent: Agent
        val basicAgent = agentRepository.findByPuuid(puuid) ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        println("enum 값 돌기 전까지 옴")
        enumValues<ValorantGameModes>().forEach {
            val agentByMatch = agentByMatchRepository.findAllByGameMode(it.toString()) ?: return
            for(i in agentByMatch.indices){
                avgDmg += agentByMatch[i].avgDmg
                kills += agentByMatch[i].kills
                deaths += agentByMatch[i].deaths
                if(agentByMatch[i].won == "true"){
                    wins += 1L
                } else{
                    losses += 1L
                }
                heads += agentByMatch[i].head
                shots += agentByMatch[i].shots
                tierList.add(agentByMatch[i].tier)
                usingAgentList.add(agentByMatch[i].agentName)
            }
            mostAgents = getMostAgent(usingAgentList)
            when(mostAgents.size){
                1 -> {
                    most1Agent = mostAgents[0].first
                }
                2 -> {
                    most1Agent = mostAgents[0].first
                    most2Agent = mostAgents[1].first
                }
                else -> {
                    most1Agent = mostAgents[0].first
                    most2Agent = mostAgents[1].first
                    most3Agent = mostAgents[2].first
                }
            }

            tierList.sortDescending()
            tier = tierList[0]
            agent = AgentReadDTO(
                name = basicAgent.name,
                puuid = puuid,
                idToken = basicAgent.id_token,
                refreshToken = basicAgent.refreshToken,
                gameMode = it,
                tier = tier,
                avgDmg = avgDmg,
                kills = kills,
                deaths = deaths,
                wins = wins,
                losses = losses,
                heads = heads,
                shots = shots,
                most1Agent = most1Agent,
                most2Agent = most2Agent,
                most3Agent = most3Agent,
            ).toEntity()
            agentRepository.save(agent)
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
        val players = matchHistory["players"] as JSONArray
        val gameMode = ValorantGameModes.assetPathToName(matchInfo["gameMode"].toString()) ?: return
        val isRanked = matchInfo["isRanked"].toString()
        val userName = agentRepository.findByPuuid(puuid)?.name ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        var rounds = 0
        var damageData: Array<Long>
        val roundResults = matchHistory["roundResults"] as JSONArray
        val killsAndDeaths = getKillsAndDeaths(players, puuid)
        val playerStats = getWonData(players, puuid, matchHistory["teams"] as JSONArray)
        val character = ValorantCharacters.characterIdToName(playerStats[1])

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

        val agentByMatch = AgentByMatchDTO(
            matchId, userName, character.toString(), gameMode.toString(), avgDmg, head, shots,
            killsAndDeaths[0], killsAndDeaths[1], playerStats[0], killsAndDeaths[2], isRanked
        ).toEntity(puuid)
        agentByMatchRepository.save(agentByMatch)
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
        return arrayOf(damage, leg, body, head)
    }

    private fun getKillsAndDeaths(players: JSONArray, puuid: String): Array<Long> {
        val stats: JSONObject
        val tier: Long
        for(player in players){
            player as JSONObject
            if(player["puuid"] != puuid){
                continue
            }
            tier = player["competitiveTier"] as Long
            stats = player["stats"] as JSONObject
            return arrayOf(stats["kills"] as Long, stats["deaths"] as Long, tier)
        }
        throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
    }

    private fun getWonData(players: JSONArray, puuid: String, teams: JSONArray): Array<String> {
        var teamId: String
        var characterId: String
        for(player in players){
            player as JSONObject
            if(player["puuid"] != puuid){
                continue
            }
            teamId = player["teamId"].toString()
            characterId = player["characterId"].toString()
            for(team in teams){
                team as JSONObject
                if(team["teamId"] == teamId){
                    return arrayOf(team["won"].toString(), characterId)
                }
            }
        }
        throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
    }
}