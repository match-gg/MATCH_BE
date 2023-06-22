package gg.match.domain.board.lol.service

import com.fasterxml.jackson.databind.ObjectMapper
import gg.match.controller.common.dto.PageResult
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.lol.repository.LoLRepository
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
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.lol.dto.*
import gg.match.domain.board.lol.repository.ChampionByMatchRepository
import gg.match.domain.chat.repository.ChatRepository
import gg.match.domain.user.entity.User
import org.springframework.data.domain.Page
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class LoLService(
    @Value("\${lol.mykey}") private val lolApiKey: String,
    private val loLRepository: LoLRepository,
    private val chatRepository: ChatRepository,
    private val summonerRepository: SummonerRepository,
    private val championByMatchRepository: ChampionByMatchRepository,
    private val objectMapper: ObjectMapper
) {
    private val serverUrl = "https://kr.api.riotgames.com"
    private val asiaServerUrl = "https://asia.api.riotgames.com"
    val parser = JSONParser()
    lateinit var puuid: String
    lateinit var result: PageResult<ReadLoLBoardDTO>

    lateinit var summonerName: String
    lateinit var summoner: Summoner
    lateinit var summonerByName: Summoner


    fun getBoards(pageable: Pageable, position: Position, type: Type, tier: Tier): PageResult<ReadLoLBoardDTO> {
        val boards: Page<LoL>
        //update expired
        checkExpire()
        //filtering
        if(position == Position.valueOf("ALL") && type == Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
            boards = loLRepository.findAllByOrderByIdDesc(pageable)
        }
        else if(position == Position.valueOf("ALL")){
            boards = if(type == Type.valueOf("ALL") && tier != Tier.valueOf("ALL")){
                loLRepository.findAllByTierOrderByIdDesc(pageable, tier)
            } else if(type != Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
                loLRepository.findAllByTypeOrderByIdDesc(pageable, type)
            } else loLRepository.findAllByTypeAndTierOrderByIdDesc(pageable, type, tier)
        }
        else if(type == Type.valueOf("ALL")){
            boards = if(tier == Tier.valueOf("ALL")){
                loLRepository.findAllByPositionOrderByIdDesc(pageable, position)
            } else  loLRepository.findAllByPositionAndTierOrderByIdDesc(pageable, position, tier)
        }
        else{
            boards = if(tier == Tier.valueOf("ALL")){
                loLRepository.findAllByPositionAndTypeOrderByIdDesc(pageable, position, type)
            } else  loLRepository.findAllByPositionAndTypeAndTierOrderByIdDesc(pageable, position, type, tier)
        }
        // boards not found
        if(boards.isEmpty) throw BusinessException(ErrorCode.NO_BOARD_FOUND)

        result = PageResult.ok(boards.map { it.toReadLoLBoardDTO(summoner, getMemberList(it.id), getBanList(it.id))})

        for(i in 0 until boards.content.size){
            summonerName = boards.content[i].name
            result.content[i].author = getSummonerByType(summonerName, boards.content[i].type).toSummonerResponseDTO()
        }
        return result
    }

    fun getBoard(boardId: Long): ReadLoLBoardDTO {
        val board = loLRepository.findById(boardId)
        return board.get().toReadLoLBoardDTO(getSummonerByType(board.get().name, board.get().type), getMemberList(boardId), getBanList(boardId))
    }

    @Transactional
    fun save(loLRequestDTO: LoLRequestDTO, user: User): Long? {
        val board = loLRepository.save(loLRequestDTO.toEntity(user.oauth2Id))
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
        val chat = chatRepository.findAllByChatRoomId(board.chatRoomId)
        loLRepository.delete(board)
        for(element in chat)
            chatRepository.delete(element)
    }

    @Transactional
    fun saveUserInfoByRiotApi(nickname: String) {
        val parser = JSONParser()
        val responseUser = getUserInfoByRiotApi(nickname)
        val responseUserId = responseUser["id"] as String
        val responseUserName = responseUser["name"] as String
        val championList: List<Pair<String, Int>>
        if (responseUserId != null) {
            isNicknameExist(responseUserName)
            val request = HttpGet("$serverUrl/lol/league/v4/entries/by-summoner/$responseUserId?api_key=$lolApiKey")
            val responseSummoner: HttpResponse = HttpClientBuilder.create().build().execute(request)
            var userJson = parser.parse(EntityUtils.toString(responseSummoner.entity, "UTF-8")) as JSONArray
            if(userJson.isEmpty()){
                throw BusinessException(ErrorCode.USER_NOT_RANKED)
            }
            else{
                for(i in 0 until userJson.size){
                    var summonerReadDTO: Summoner = objectMapper.readValue(userJson[i].toString(), SummonerReadDTO::class.java).toEntity()
                    if("TFT" in summonerReadDTO.queueType) continue
                    summonerRepository.save(summonerReadDTO)
                }
            }
            championList = getMostChampions(responseUserName)
            when(summonerRepository.countBySummonerName(responseUserName)){
                0L -> return
                1L -> summonerByName = summonerRepository.findBySummonerName(responseUserName)
                2L -> {
                    summonerByName = summonerRepository.findBySummonerNameAndQueueType(responseUserName, "RANKED_FLEX_SR")
                    summoner = summonerRepository.findBySummonerNameAndQueueType(responseUserName, "RANKED_SOLO_5x5")
                }
            }
            if(summonerByName != null){
                summonerByName.updateChampion(championList)
            }
            if(summoner != null){
                summoner.updateChampion(championList)
            }
        }
    }

    fun getUserInfoByRiotApi(nickname: String): JSONObject {
        val request = HttpGet("$serverUrl/lol/summoner/v4/summoners/by-name/${nickname.trim().replace(" ", "+")}?api_key=$lolApiKey")
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
        return riotUserJson
    }

    fun isNicknameExist(responseUserName: String){
        if(summonerRepository.existsBySummonerName(responseUserName)){
            summonerRepository.deleteAllBySummonerName(responseUserName)
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
        var mostLane = mutableListOf<String>()
        var championMap = mutableMapOf<String, Int>()
        var laneMap = mutableMapOf<String, Int>()
        for(i in 0 until matchListJson.size){
            if(championByMatchRepository.existsByMatchIdAndSummonerName(matchListJson[i] as String, summonerName)) continue
            getChampionInMatchBySummonerName(matchListJson[i] as String)
        }
        var championList: List<Champion> = championByMatchRepository.findAllBySummonerName(summonerName)
        if(championList.isEmpty()){
            return listOf(Pair("poro", 1))
        }
        for(element in championList){
            usingChampionList.add(element.champion)
            mostLane.add(element.mostLane)
        }
        var championSet: Set<String> = HashSet<String>(usingChampionList)
        var laneSet: Set<String> = HashSet<String>(mostLane)

        for(championName: String in championSet)
            championMap[championName] = Collections.frequency(usingChampionList, championName)
        for(lane: String in laneSet)
            laneMap[lane] = Collections.frequency(mostLane, lane)

        //map to list
        val championMapToList = championMap.toList().sortedByDescending { it.second }
        val laneMapToList = laneMap.toList().sortedByDescending { it.second }

        //save most lane in summoner
        var summonerList = summonerRepository.findAllBySummonerName(summonerName)
        for(element in summonerList) {
            element.updateLane(laneMapToList[0].first)
        }
        return championMapToList
    }

    fun getMatchList(summonerName: String): JSONArray{
        val request = HttpGet("$asiaServerUrl/lol/match/v5/matches/by-puuid/$puuid/ids?start=0&count=30&api_key=$lolApiKey")
        val matchList = HttpClientBuilder.create().build().execute(request)
        if(matchList.statusLine.statusCode != 200){
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        return parser.parse(EntityUtils.toString(matchList.entity, "UTF-8")) as JSONArray
    }

    @Transactional
    fun getChampionInMatchBySummonerName(matchId: String) {
        val request = HttpGet("$asiaServerUrl/lol/match/v5/matches/$matchId?api_key=$lolApiKey")
        val response = HttpClientBuilder.create().build().execute(request)
        if(response.statusLine.statusCode != 200){
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        val jsonString = EntityUtils.toString(response.entity, "UTF-8")
        var gson = Gson()
        var sample = gson.fromJson(jsonString, MatchDTO::class.java)
        if(sample.info.gameMode == "ARAM")  return
        var championByMatch = ChampionByMatchDTO(
            summonerName,
            sample.info.participants.filter { it.summonerName == summonerName }.map { it.championName }[0],
            matchId,
            sample.info.participants.filter { it.summonerName == summonerName }.map { it.teamPosition }[0]
        ).toEntity()
        championByMatchRepository.save(championByMatch)
    }

    fun getMemberList(boardId: Long): List<String>{
        val board = loLRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomId(board.get().chatRoomId)
        var memberList = mutableListOf<String>()
        for(element in chatRooms){
            if(element.oauth2Id == "banned")   continue
            element.nickname?.let { memberList.add(it) }
        }
        return memberList
    }

    fun getBanList(boardId: Long): List<String>{
        val board = loLRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomIdAndOauth2Id(board.get().chatRoomId, "banned")
        var banList = mutableListOf<String>()
        for(element in chatRooms){
            element.nickname?.let { banList.add(it) }
        }
        return banList
    }

    @Transactional
    fun checkExpire(){
        val boards = loLRepository.findAll()
        var expiredTime: LocalDateTime
        for(i in 0 until boards.size){
            if(boards[i].isExpired == "true") continue
            expiredTime = when(boards[i].expire){
                Expire.FIFTEEN_M -> boards[i].created.plusMinutes(15)
                Expire.THIRTY_M -> boards[i].created.plusMinutes(30)
                Expire.ONE_H -> boards[i].created.plusHours(1)
                Expire.TWO_H -> boards[i].created.plusHours(2)
                Expire.THREE_H -> boards[i].created.plusHours(3)
                Expire.SIX_H -> boards[i].created.plusHours(6)
                Expire.TWELVE_H -> boards[i].created.plusHours(12)
                Expire.TWENTY_FOUR_H -> boards[i].created.plusDays(1)
            }
            if(expiredTime <= LocalDateTime.now())
                boards[i].isExpiredUpdate("true")
        }
    }
}