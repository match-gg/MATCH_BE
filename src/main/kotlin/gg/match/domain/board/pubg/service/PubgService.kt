package gg.match.domain.board.pubg.service

import com.fasterxml.jackson.databind.ObjectMapper
import gg.match.common.dto.ChatMemberListDTO
import gg.match.controller.common.dto.PageResult
import gg.match.controller.common.entity.Expire
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.pubg.dto.PlayerReadDTO
import gg.match.domain.board.pubg.dto.PlayerResponseDTO
import gg.match.domain.board.pubg.dto.PubgRequestDTO
import gg.match.domain.board.pubg.dto.ReadPubgBoardDTO
import gg.match.domain.board.pubg.entity.*
import gg.match.domain.board.pubg.repository.PlayerRepository
import gg.match.domain.board.pubg.repository.PubgRepository
import gg.match.domain.chat.repository.ChatRepository
import gg.match.domain.user.entity.User
import gg.match.domain.user.repository.FollowRepository
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
    fun getFollowerBoards(user: User, pageable: Pageable, oauth2Ids: List<String>): PageResult<ReadPubgBoardDTO>{
        val boards = pubgRepository.findAllByOauth2IdInAndExpiredAndFinishedOrderByIdDesc(pageable, oauth2Ids, "false", "false")
        if(boards.isEmpty)  throw BusinessException(ErrorCode.NO_BOARD_FOUND)
        result = PageResult.ok(boards.map { it.toReadPubgBoardDTO(playerRepository.findByNameAndPlatformAndType(it.name, it.platform, it.type), getMemberList(it.id), getBanList(it.id))})

        for(i in 0 until boards.content.size){
            val playerName = boards.content[i].name
            result.content[i].author = getPlayerByPlatformAndType(playerName, boards.content[i].platform, boards.content[i].type)
        }
        return result
    }

    @Transactional
    fun getBoards(pageable: Pageable, platform: Platform, type: Type, tier: Tier): PageResult<ReadPubgBoardDTO> {
        val boards: Page<Pubg>
        //update expired
        updateExpired()
        //filtering
        if(platform == Platform.valueOf("ALL") && type == Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
            boards = pubgRepository.findAllByOrderByExpiredAscIdDesc(pageable)
        }
        else if(platform == Platform.valueOf("ALL")){
            boards = if(type == Type.valueOf("ALL") && tier != Tier.valueOf("ALL")){
                pubgRepository.findAllByTierOrderByExpiredAscIdDesc(pageable, tier)
            } else if(type != Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
                pubgRepository.findAllByTypeOrderByExpiredAscIdDesc(pageable, type)
            } else pubgRepository.findAllByTypeAndTierOrderByExpiredAscIdDesc(pageable, type, tier)
        }
        else if(type == Type.valueOf("ALL")){
            boards = if(tier == Tier.valueOf("ALL")){
                pubgRepository.findAllByPlatformOrderByExpiredAscIdDesc(pageable, platform)
            } else  pubgRepository.findAllByPlatformAndTierOrderByExpiredAscIdDesc(pageable, platform, tier)
        }
        else{
            boards = if(tier == Tier.valueOf("ALL")){
                pubgRepository.findAllByPlatformAndTypeOrderByExpiredAscIdDesc(pageable, platform, type)
            } else  pubgRepository.findAllByPlatformAndTypeAndTierOrderByExpiredAscIdDesc(pageable, platform, type, tier)
        }
        // boards not found
        if(boards.isEmpty)  throw BusinessException(ErrorCode.NO_BOARD_FOUND)
        result = PageResult.ok(boards.map { it.toReadPubgBoardDTO(playerRepository.findByNameAndPlatformAndType(it.name, it.platform, it.type), getMemberList(it.id), getBanList(it.id))})

        for(i in 0 until boards.content.size){
            val playerName = boards.content[i].name
            result.content[i].author = getPlayerByPlatformAndType(playerName, boards.content[i].platform, boards.content[i].type)
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
        board.name = pubgRequestDTO.name
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
        val account = getPlayerAccountId(nickname, platform)
        savePlayerInfo(nickname, account, platform)
    }

    fun getPlayerAccountId(nickname: String, platform: Platform): String{
        try {
            val request = HttpGet(
                "https://api.pubg.com/shards/${
                    platform.toString().lowercase()
                }/players?filter[playerNames]=${nickname}"
            )
            val responseJson = getResponse(request)
            val dataJson = responseJson["data"] as JSONArray
            val idInfo = dataJson[0] as JSONObject
            return idInfo["id"] as String
        } catch (e: Exception) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
    }

    @Transactional
    fun savePlayerInfo(nickname: String, account: String, platform: Platform) {
        val request = HttpGet("https://api.pubg.com/shards/${platform.toString().lowercase()}/players/$account/seasons/division.bro.official.pc-2018-25")
        val responseJson = getResponse(request)
        val dataJson = responseJson["data"] as JSONObject
        val attributes = dataJson["attributes"] as JSONObject
        val gameModeStats = attributes["gameModeStats"] as JSONObject

        //delete old info before save
        if(playerRepository.existsByName(nickname)) {
            playerRepository.deleteAllByName(nickname)
        }

        //save duo info
        val duo = gameModeStats["duo"] as JSONObject
        duo["tier"] = "None"
        duo["subTier"] = "None"
        duo["currentRankPoint"] = 0
        val playerByDuo: Player = objectMapper.readValue(duo.toString(), PlayerReadDTO::class.java)
            .toEntity(nickname, platform, Type.DUO)
        playerRepository.save(playerByDuo)

        //save squad info
        val squad = gameModeStats["squad"] as JSONObject
        squad["tier"] = "None"
        squad["subTier"] = "None"
        squad["currentRankPoint"] = 0
        val playerBySquad: Player = objectMapper.readValue(squad.toString(), PlayerReadDTO::class.java)
            .toEntity(nickname, platform, Type.SQUAD)
        playerRepository.save(playerBySquad)

        //save ranked_squad info
        saveRankedSquadInfo(nickname, account, platform)
    }

    @Transactional
    fun saveRankedSquadInfo(nickname: String, account: String, platform: Platform){
        val request = HttpGet("https://api.pubg.com/shards/${platform.toString().lowercase()}/players/$account/seasons/division.bro.official.pc-2018-25/ranked")
        val responseJson = getResponse(request)
        val dataJson = responseJson["data"] as JSONObject
        val attributes = dataJson["attributes"] as JSONObject
        val rankedGameModeStats = attributes["rankedGameModeStats"] as JSONObject
        val squad = rankedGameModeStats["squad"] as JSONObject?
        if(squad == null){
            val player = Player(0, platform, nickname, "None", "None", 0, Type.RANKED_SQUAD, 0, 0, 0F, 0, 0, 0)
            playerRepository.save(player)
            return
        }
        val currentTier = squad["currentTier"] as JSONObject
        squad["tier"] = currentTier["tier"] as String
        squad["subTier"] = currentTier["subTier"] as String
        val playerByRankedSquad: Player = objectMapper.readValue(squad.toString(), PlayerReadDTO::class.java)
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

    fun getMemberList(boardId: Long): List<ChatMemberListDTO>{
        val board = pubgRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomIdAndIsBanned(board.get().chatRoomId, "false")
        val memberList = mutableListOf<ChatMemberListDTO>()
        for(element in chatRooms){
            element.nickname?.let { memberList.add(ChatMemberListDTO(element.oauth2Id, it)) }
        }
        return memberList
    }

    fun getBanList(boardId: Long): List<ChatMemberListDTO>{
        val board = pubgRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomIdAndIsBanned(board.get().chatRoomId, "true")
        val banList = mutableListOf<ChatMemberListDTO>()
        for(element in chatRooms){
            element.nickname?.let { banList.add(ChatMemberListDTO(element.oauth2Id, it)) }
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