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
import gg.match.domain.board.lol.dto.*
import gg.match.domain.board.lol.repository.ChampionByMatchRepository
import gg.match.domain.chat.repository.ChatRepository
import gg.match.domain.user.entity.User
import org.springframework.data.domain.Page

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

    //init <- 관리자로 초기화
    var summonerName: String = "수유욱"
    var summoner: Summoner = summonerRepository.findBySummonerNameAndQueueType(summonerName, "RANKED_SOLO_5x5")
    var summonerByName: Summoner = summoner


    fun getBoards(pageable: Pageable, position: Position, type: Type, tier: Tier): PageResult<ReadLoLBoardDTO> {
        val boards: Page<LoL>
        //filtering
        if(position == Position.valueOf("ALL") && type == Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
            boards = loLRepository.findAll(pageable)
        }
        else if(position == Position.valueOf("ALL")){
            boards = if(type == Type.valueOf("ALL") && tier != Tier.valueOf("ALL")){
                loLRepository.findAllByTier(pageable, tier)
            } else if(type != Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
                loLRepository.findAllByType(pageable, type)
            } else loLRepository.findAllByTypeAndTier(pageable, type, tier)
        }
        else if(type == Type.valueOf("ALL")){
            boards = if(tier == Tier.valueOf("ALL")){
                loLRepository.findAllByPosition(pageable, position)
            } else  loLRepository.findAllByPositionAndTier(pageable, position, tier)
        }
        else{
            boards = if(tier == Tier.valueOf("ALL")){
                loLRepository.findAllByPositionAndType(pageable, position, type)
            } else  loLRepository.findAllByPositionAndTypeAndTier(pageable, position, type, tier)
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
                    var summonerReadDTO: Summoner = objectMapper.readValue(userJson[i].toString(), SummonerReadDTO::class.java).toEntity()
                    if("TFT" in summonerReadDTO.queueType) continue
                    summonerRepository.save(summonerReadDTO)
                }
            }
            championList = getMostChampions(nickname)
            when(summonerRepository.countBySummonerName(nickname)){
                0L -> return
                1L -> summonerByName = summonerRepository.findBySummonerName(nickname)
                2L -> {
                    summonerByName = summonerRepository.findBySummonerNameAndQueueType(nickname, "RANKED_FLEX_SR")
                    summoner = summonerRepository.findBySummonerNameAndQueueType(nickname, "RANKED_SOLO_5x5")
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

    fun getUserInfoByRiotApi(nickname: String): Any? {
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
        var mostLane = mutableListOf<String>()
        var championMap = mutableMapOf<String, Int>()
        var laneMap = mutableMapOf<String, Int>()
        for(i in 0 until matchListJson.size){
            if(championByMatchRepository.existsByMatchIdAndSummonerName(matchListJson[i] as String, summonerName)) continue
            getChampionInMatchBySummonerName(matchListJson[i] as String)
        }
        var championList: List<Champion> = championByMatchRepository.findAllBySummonerName(summonerName)
        for(element in championList){
            usingChampionList.add(element.champion)
            mostLane.add(element.lane)
        }
        var championSet: Set<String> = HashSet<String>(usingChampionList)
        var laneSet: Set<String> = HashSet<String>(mostLane)

        for(str: String in championSet)
            championMap[str] = Collections.frequency(usingChampionList, str)
        for(str: String in laneSet)
            laneMap[str] = Collections.frequency(laneSet, str)

        //map to list
        val championMapToList = championMap.toList()
        val laneMapToList = laneMap.toList()
        championMapToList.sortedByDescending { it.second }
        laneMapToList.sortedByDescending { it.second }
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
            sample.info.participants.filter { it.summonerName == summonerName }.map { it.lane }[0]
        ).toEntity()
        println(sample.info.participants.filter { it.summonerName == summonerName }.map { it.lane }[0])
        championByMatchRepository.save(championByMatch)
    }

    fun getMemberList(boardId: Long): List<String>{
        val board = loLRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomId(board.get().chatRoomId)
        var memberList = mutableListOf<String>()
        for(element in chatRooms){
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
}