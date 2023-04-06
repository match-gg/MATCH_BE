package gg.match.domain.board.lol.service

import com.fasterxml.jackson.databind.ObjectMapper
import gg.match.controller.common.dto.PageResult
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.lol.repository.LoLRepository
import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.dto.MatchDTO
import gg.match.domain.board.lol.dto.ReadLoLBoardDTO
import gg.match.domain.board.lol.dto.SummonerReadDTO
import gg.match.domain.board.lol.entity.*
import gg.match.domain.board.lol.repository.SummonerRepository
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.collections.HashSet
import com.google.gson.Gson

@Service
@Transactional(readOnly = true)
class LoLService(
    @Value("\${lol.mykey}") private val lolApiKey: String,
    private val loLRepository: LoLRepository,
    private val summonerRepository: SummonerRepository,
    private val objectMapper: ObjectMapper
) {
    private val serverUrl = "https://kr.api.riotgames.com"
    private val asiaServerUrl = "https://asia.api.riotgames.com"
    val parser = JSONParser()
    lateinit var puuid: String
    lateinit var result: PageResult<ReadLoLBoardDTO>

    //init <- 관리자로 초기화
    var summonerName: String = "수유욱"
    var summoner: Summoner = summonerRepository.findBySummonerNameAndQueueType(summonerName, "RANKED_SOLO_5x5")
    var summonerByName: Summoner = summoner


    fun getBoards(pageable: Pageable, position: Position, type: Type, tier: Tier): PageResult<ReadLoLBoardDTO> {
        val boards = if(type == Type.valueOf("ALL")){
            loLRepository.findByPositionAndTier(pageable, position, tier)
        } else{
            loLRepository.findByPositionAndTypeAndTier(pageable, position, type, tier)
        }
        if(boards.isEmpty) throw BusinessException(ErrorCode.NO_BOARD_FOUND)
        result = PageResult.ok(boards.map { it.toReadLoLBoardDTO(summoner) })

        for(i in 0 until boards.content.size){
            summonerName = boards.content[i].name
            result.content[i].author = getSummonerByType(summonerName, boards.content[i].type).toSummonerResponseDTO()
        }
        return result
    }

    fun getBoard(boardId: Long): ReadLoLBoardDTO {
        val board = loLRepository.findById(boardId)
        return board.get().toReadLoLBoardDTO(getSummonerByType(board.get().name, board.get().type))
    }

    @Transactional
    fun save(loLRequestDTO: LoLRequestDTO): Long? {
        val board = loLRepository.save(loLRequestDTO.toEntity())
        return board.id
    }

    @Transactional
    fun update(boardId: Long, loLRequestDTO: LoLRequestDTO): LoL {
        val board = loLRepository.findByIdOrNull(boardId)
            ?: throw Exception("not found")

        board.update(loLRequestDTO)
        return board
    }

    @Transactional
    fun delete(boardId: Long) {
        val board = loLRepository.findByIdOrNull(boardId)
            ?: throw Exception("not found")
        loLRepository.delete(board)
    }

    @Transactional
    fun saveUserInfoByRiotApi(nickname: String) {
        val parser = JSONParser()
        val responseUser = getUserInfoByRiotApi(nickname)
        val championList: List<Pair<String, Int>>
        if (responseUser != null) {
            isNicknameExist(nickname)
            val request = HttpGet("$serverUrl/lol/league/v4/entries/by-summoner/$responseUser?api_key=$lolApiKey")
            val responseSummoner: HttpResponse = HttpClientBuilder.create().build().execute(request)
            var userJson = parser.parse(EntityUtils.toString(responseSummoner.entity, "UTF-8")) as JSONArray
            if(userJson.isEmpty()){
                throw BusinessException(ErrorCode.USER_NOT_RANKED)
            }
            else{
                for(i in 0 until userJson.size){
                    summonerRepository.save(objectMapper.readValue(userJson[i].toString(), SummonerReadDTO::class.java).toEntity())
                }
            }
            championList = getMostChampions(nickname)
            println(championList)
            when(summonerRepository.countBySummonerName(nickname)){
                0L -> return
                1L -> summonerByName = summonerRepository.findBySummonerName(nickname)
                2L -> {
                    summonerByName = summonerRepository.findBySummonerNameAndQueueType(nickname, "RANKED_FLEX_SR")
                    summoner = summonerRepository.findBySummonerNameAndQueueType(nickname, "RANKED_SOLO_5x5")
                }
            }
            if(summonerByName != null){
                summonerByName.update(championList[0].first, championList[1].first, championList[2].first)
            }
            if(summoner != null){
                summoner.update(championList[0].first, championList[1].first, championList[2].first)
            }
        }
    }

    fun getUserInfoByRiotApi(nickname: String): Any? {
        val request = HttpGet("$serverUrl/lol/summoner/v4/summoners/by-name/${nickname.trim().replace(" ", "")}?api_key=$lolApiKey")
        val riotUser = HttpClientBuilder.create().build().execute(request)
        if(riotUser.statusLine.statusCode == 404){
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        if(riotUser.statusLine.statusCode != 200){
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        val riotUserJson = parser.parse(EntityUtils.toString(riotUser.entity, "UTF-8")) as JSONObject
        puuid = riotUserJson["puuid"] as String
        summonerName = riotUserJson["name"] as String
        return riotUserJson["id"]
    }

    fun isNicknameExist(nickname: String){
        if(summonerRepository.existsBySummonerName(nickname)){
            summonerRepository.deleteBySummonerName(nickname)
        }
    }

    fun getSummonerByType(summonerName: String, type: Type): Summoner{
        return if(type == Type.FREE_RANK){
            summonerRepository.findBySummonerNameAndQueueType(summonerName, "RANKED_FLEX_SR")
        }
        else summonerRepository.findBySummonerNameAndQueueType(summonerName, "RANKED_SOLO_5x5")
    }

    fun getMostChampions(summonerName: String): List<Pair<String, Int>> {
        val matchListJson = getMatchList(summonerName)
        var usingChampionList = mutableListOf<String>()
        var map = mutableMapOf<String, Int>()
        for(i in 0 until matchListJson.size){
            getChampionInMatchBySummonerName(matchListJson[i] as String, usingChampionList)
        }

        var set: Set<String> = HashSet<String>(usingChampionList)

        for(str: String in set){
            map[str] = Collections.frequency(usingChampionList, str)
        }
        //map to list
        val mapToList = map.toList()
        return mapToList.sortedByDescending { it.second }
    }

    fun getMatchList(summonerName: String): JSONArray{
        val request = HttpGet("$asiaServerUrl/lol/match/v5/matches/by-puuid/$puuid/ids?start=0&count=30&api_key=$lolApiKey")
        val matchList = HttpClientBuilder.create().build().execute(request)
        if(matchList.statusLine.statusCode != 200){
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        return parser.parse(EntityUtils.toString(matchList.entity, "UTF-8")) as JSONArray
    }

    fun getChampionInMatchBySummonerName(matchId: String, usingChampionList: MutableList<String>) {
        val request = HttpGet("$asiaServerUrl/lol/match/v5/matches/$matchId?api_key=$lolApiKey")
        val response = HttpClientBuilder.create().build().execute(request)
        if(response.statusLine.statusCode != 200){
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        val jsonString = EntityUtils.toString(response.entity, "UTF-8")
        var gson = Gson()
        var sample = gson.fromJson(jsonString, MatchDTO::class.java)
        println(sample.info.gameMode)
        if(sample.info.gameMode == "ARAM")  return
        println(sample.info.participants.filter { it.summonerName == summonerName }.map { it.championName }[0])
        usingChampionList.add(sample.info.participants.filter { it.summonerName == summonerName }.map { it.championName }[0])
    }
}