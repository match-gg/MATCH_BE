package gg.match.domain.board.pubg.service

import com.fasterxml.jackson.databind.ObjectMapper
import gg.match.controller.common.dto.PageResult
import gg.match.controller.common.entity.Expire
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.dto.ReadLoLBoardDTO
import gg.match.domain.board.lol.entity.LoL
import gg.match.domain.board.pubg.dto.PlayerReadDTO
import gg.match.domain.board.pubg.dto.PlayerResponseDTO
import gg.match.domain.board.pubg.dto.PubgRequestDTO
import gg.match.domain.board.pubg.dto.ReadPubgBoardDTO
import gg.match.domain.board.pubg.entity.*
import gg.match.domain.board.pubg.repository.PlayerRepository
import gg.match.domain.board.pubg.repository.PubgRepository
import gg.match.domain.chat.repository.ChatRepository
import gg.match.domain.user.entity.User
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class PubgService(
    @Value("\${pubg.mykey}") private val pubgApiKey: String,
    private val pubgRepository: PubgRepository,
    private val playerRepository: PlayerRepository,
    private val chatRepository: ChatRepository,
    private val objectMapper: ObjectMapper
){
    lateinit var result: PageResult<ReadPubgBoardDTO>
    val parser = JSONParser()

    @Transactional
    fun getBoards(pageable: Pageable, platform: Platform, type: Type, tier: Tier): PageResult<ReadPubgBoardDTO> {
        val boards: Page<Pubg>
        //update expired
        updateExpired()
        //filtering
        if(platform == Platform.valueOf("ALL") && type == Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
            boards = pubgRepository.findAllByOrderByIdDesc(pageable)
        }
        else if(platform == Platform.valueOf("ALL")){
            boards = if(type == Type.valueOf("ALL") && tier != Tier.valueOf("ALL")){
                pubgRepository.findAllByTierOrderByIdDesc(pageable, tier)
            } else if(type != Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
                pubgRepository.findAllByTypeOrderByIdDesc(pageable, type)
            } else pubgRepository.findAllByTypeAndTierOrderByIdDesc(pageable, type, tier)
        }
        else if(type == Type.valueOf("ALL")){
            boards = if(tier == Tier.valueOf("ALL")){
                pubgRepository.findAllByPlatformOrderByIdDesc(pageable, platform)
            } else  pubgRepository.findAllByPlatformAndTierOrderByIdDesc(pageable, platform, tier)
        }
        else{
            boards = if(tier == Tier.valueOf("ALL")){
                pubgRepository.findAllByPlatformAndTypeOrderByIdDesc(pageable, platform, type)
            } else  pubgRepository.findAllByPlatformAndTypeAndTierOrderByIdDesc(pageable, platform, type, tier)
        }
        // boards not found
        if(boards.isEmpty) throw BusinessException(ErrorCode.NO_BOARD_FOUND)
//        result = PageResult.ok(boards.map { it.toReadPubgBoardDTO(playerRepository.findByIdAndTier(0, Tier.valueOf("DIAMOND")), getMemberList(it.id), getBanList(it.id))})

        result = PageResult.ok(boards.map { it.toReadPubgBoardDTO(playerRepository.findById(0).get().toPlayerResponseDTO(), getMemberList(it.id), getBanList(it.id))})

        for(i in 0 until boards.content.size){
            result.content[i].author = getPlayerByPlatformAndType("Dsquad2", Platform.STEAM, Type.DUO)
        }
        return result
    }

    fun getBoard(boardId: Long): ReadPubgBoardDTO {
        val board = pubgRepository.findById(boardId).get()
        return board.toReadPubgBoardDTO(getPlayerByPlatformAndType(board.name, board.platform, board.type), getMemberList(boardId), getBanList(boardId))
    }

    @Transactional
    fun save(pubgRequestDTO: PubgRequestDTO, user: User): Long? {
        val board = pubgRepository.save(pubgRequestDTO.toEntity(user.oauth2Id))
        return board.id
    }

    @Transactional
    fun update(boardId: Long, pubgRequestDTO: PubgRequestDTO): Pubg {
        val board = pubgRepository.findByIdOrNull(boardId)
            ?: throw Exception("not found")

        board.update(pubgRequestDTO)
        return board
    }

    @Transactional
    fun delete(boardId: Long) {
        val board = pubgRepository.findByIdOrNull(boardId)
            ?: throw Exception("not found")
        val chat = chatRepository.findAllByChatRoomId(board.chatRoomId)
        pubgRepository.delete(board)
        for(element in chat)
            chatRepository.delete(element)
    }


    //DB에서 플레이어 정보 가져오기 (현재 시즌 user가 없는 관계로 우선 19시즌으로 작업)
    fun getPlayerByPlatformAndType(nickname: String, platform: Platform, type: Type): PlayerResponseDTO {
        return try{
            playerRepository.findByNameAndPlatformAndType(nickname, platform, type)
        } catch (e: Exception){
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
    }

    //배그에서 플레이어 정보 가져와 저장하기 (현재 시즌 user가 없는 관계로 우선 19시즌으로 작업)
    @Transactional
    fun getPlayerInfoByPubgApi(nickname: String, platform: Platform){
        var account = getPlayerAccountId(nickname, platform)
        savePlayerInfo(nickname, account, platform)
    }

    fun getPlayerAccountId(nickname: String, platform: Platform): String{
        try {
            val request = HttpGet(
                "https://api.pubg.com/shards/${
                    platform.toString().lowercase()
                }/players?filter[playerNames]=${nickname}"
            )
            var responseJson = getResponse(request)
            var dataJson = responseJson["data"] as JSONArray
            var idInfo = dataJson[0] as JSONObject
            return idInfo["id"] as String
        } catch (e: Exception) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
    }

    @Transactional
    fun savePlayerInfo(nickname: String, account: String, platform: Platform) {
        val request = HttpGet("https://api.pubg.com/shards/${platform.toString().lowercase()}/players/$account/seasons/division.bro.official.pc-2018-19")
        var responseJson = getResponse(request)
        val dataJson = responseJson["data"] as JSONObject
        val attributes = dataJson["attributes"] as JSONObject
        val gameModeStats = attributes["gameModeStats"] as JSONObject

        //delete old info before save
        if(playerRepository.existsByName(nickname)) {
            playerRepository.deleteAllByName(nickname)
        }

        //save duo info
        var duo = gameModeStats["duo"] as JSONObject
        duo["tier"] = "None"
        duo["subTier"] = "None"
        duo["currentRankPoint"] = 0
        var playerByDuo: Player = objectMapper.readValue(duo.toString(), PlayerReadDTO::class.java)
            .toEntity(nickname, platform, Type.DUO)
        playerRepository.save(playerByDuo)

        //save squad info
        var squad = gameModeStats["squad"] as JSONObject
        squad["tier"] = "None"
        squad["subTier"] = "None"
        squad["currentRankPoint"] = 0
        var playerBySquad: Player = objectMapper.readValue(squad.toString(), PlayerReadDTO::class.java)
            .toEntity(nickname, platform, Type.SQUAD)
        playerRepository.save(playerBySquad)

        //save ranked_squad info
        saveRankedSquadInfo(nickname, account, platform)
    }

    @Transactional
    fun saveRankedSquadInfo(nickname: String, account: String, platform: Platform){
        val request = HttpGet("https://api.pubg.com/shards/${platform.toString().lowercase()}/players/$account/seasons/division.bro.official.pc-2018-19/ranked")
        var responseJson = getResponse(request)
        val dataJson = responseJson["data"] as JSONObject
        val attributes = dataJson["attributes"] as JSONObject
        val rankedGameModeStats = attributes["rankedGameModeStats"] as JSONObject
        var squad = rankedGameModeStats["squad"] as JSONObject?
        if(squad == null){
            val player = Player(0, platform, nickname, "None", "None", 0, Type.RANKED_SQUAD, 0, 0, 0F, 0, 0, 0)
            playerRepository.save(player)
            return
        }
        var currentTier = squad["currentTier"] as JSONObject
        squad["tier"] = currentTier["tier"] as String
        squad["subTier"] = currentTier["subTier"] as String
        var playerByRankedSquad: Player = objectMapper.readValue(squad.toString(), PlayerReadDTO::class.java)
            .toEntity(nickname, platform, Type.RANKED_SQUAD)
        playerRepository.save(playerByRankedSquad)
    }

    fun getResponse(request: HttpGet): JSONObject{
        request.addHeader("Authorization", "Bearer $pubgApiKey")
        request.addHeader("Accept", "application/vnd.api+json")
        val response = HttpClientBuilder.create().build().execute(request)
        if(response.statusLine.statusCode != 200){
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        return parser.parse(EntityUtils.toString(response.entity, "UTF-8")) as JSONObject
    }

    fun getMemberList(boardId: Long): List<String>{
        val board = pubgRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomId(board.get().chatRoomId)
        var memberList = mutableListOf<String>()
        for(element in chatRooms){
            if(element.oauth2Id == "banned")   continue
            element.nickname?.let { memberList.add(it) }
        }
        return memberList
    }

    fun getBanList(boardId: Long): List<String>{
        val board = pubgRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomIdAndOauth2Id(board.get().chatRoomId, "banned")
        var banList = mutableListOf<String>()
        for(element in chatRooms){
            element.nickname?.let { banList.add(it) }
        }
        return banList
    }

    @Transactional
    fun updateExpired(){
        val boards = pubgRepository.findAll()
        var expiredTime: LocalDateTime
        val now = LocalDateTime.now().plusHours(9)
        for(i in 0 until boards.size){

            if(boards[i].expired == "true")
                continue

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

            if(expiredTime.isBefore(now)) {
                boards[i].update("true")
            }
        }
    }
}