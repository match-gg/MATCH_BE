package gg.match.domain.board.overwatch.service

import gg.match.controller.common.dto.PageResult
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.overwatch.entity.*
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.overwatch.dto.*
import gg.match.domain.board.overwatch.dto.ReadOverwatchBoardDTO
import gg.match.domain.board.overwatch.repository.HeroRepository
import gg.match.domain.board.overwatch.repository.OverwatchRepository
import gg.match.domain.chat.repository.ChatRepository
import gg.match.domain.user.entity.User
import org.springframework.data.domain.Page
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class OverwatchService(
    private val overwatchRepository: OverwatchRepository,
    private val chatRepository: ChatRepository,
    private val heroRepository: HeroRepository
) {
    private val serverUrl = "https://ow-api.com/v1/stats/pc/asia/"

    @Transactional
    fun getBoards(pageable: Pageable, position: Position, type: Type, tier: Tier): PageResult<ReadOverwatchBoardDTO> {
        val boards: Page<Overwatch>
        var name: String
        //update expired
        updateExpired()
        //filtering
        if(position == Position.valueOf("ALL") && type == Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
            boards = overwatchRepository.findAllByOrderByIdDesc(pageable)
        }
        else if(position == Position.valueOf("ALL")){
            boards = if(type == Type.valueOf("ALL") && tier != Tier.valueOf("ALL")){
                overwatchRepository.findAllByTierOrderByIdDesc(pageable, tier)
            } else if(type != Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
                overwatchRepository.findAllByTypeOrderByIdDesc(pageable, type)
            } else overwatchRepository.findAllByTypeAndTierOrderByIdDesc(pageable, type, tier)
        }
        else if(type == Type.valueOf("ALL")){
            boards = if(tier == Tier.valueOf("ALL")){
                overwatchRepository.findAllByPositionOrderByIdDesc(pageable, position)
            } else  overwatchRepository.findAllByPositionAndTierOrderByIdDesc(pageable, position, tier)
        }
        else{
            boards = if(tier == Tier.valueOf("ALL")){
                overwatchRepository.findAllByPositionAndTypeOrderByIdDesc(pageable, position, type)
            } else  overwatchRepository.findAllByPositionAndTypeAndTierOrderByIdDesc(pageable, position, type, tier)
        }
        // boards not found
        if(boards.isEmpty) throw BusinessException(ErrorCode.NO_BOARD_FOUND)

        var result = PageResult.ok(boards.map { it.toReadOverwatchBoardDTO(heroRepository.findByName(it.name).toHeroResponseDTO(), getMemberList(it.id), getBanList(it.id))})

        for(i in 0 until boards.content.size){
            name = boards.content[i].name
            result.content[i].author = getHeroByName(name)
        }
        return result
    }

    fun getBoard(boardId: Long): ReadOverwatchBoardDTO {
        val board = overwatchRepository.findById(boardId)
        return board.get().toReadOverwatchBoardDTO(getHeroByName(board.get().name), getMemberList(boardId), getBanList(boardId))
    }

    @Transactional
    fun save(overwatchRequestDTO: OverwatchRequestDTO, user: User): Long? {
        val board = overwatchRepository.save(overwatchRequestDTO.toEntity(user.oauth2Id))
        board.name = overwatchRequestDTO.name
        return board.id
    }

    @Transactional
    fun update(boardId: Long, overwatchRequestDTO: OverwatchRequestDTO): Overwatch {
        val board = overwatchRepository.findByIdOrNull(boardId)
            ?: throw Exception("not found")

        board.update(overwatchRequestDTO)
        return board
    }

    @Transactional
    fun delete(boardId: Long) {
        val board = overwatchRepository.findByIdOrNull(boardId)
            ?: throw Exception("not found")
        val chat = chatRepository.findAllByChatRoomId(board.chatRoomId)
        overwatchRepository.delete(board)
        for(element in chat)
            chatRepository.delete(element)
    }

    fun getHeroByName(name: String): HeroResponseDTO{
        return try{
            heroRepository.findByName(name).toHeroResponseDTO()
        } catch (e: Exception){
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    fun getHeroInfo(name: String, battletag: Long, type: Type): HeroResponseDTO{
        return try{
            heroRepository.findByNameAndBattletagAndType(name, battletag, type).toHeroResponseDTO()
        } catch (e: Exception){
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    @Transactional
    fun saveHeroInfoByBattleNetApi(name: String, battletag: Long) {
        deleteOldHero(name, battletag)
        val parser = JSONParser()
        var mostHeroList: List<Pair<String, String>>
        val request = HttpGet("$serverUrl$name-$battletag/complete")
        val response = HttpClientBuilder.create().build().execute(request)
        var responseJson = parser.parse(EntityUtils.toString(response.entity, "UTF-8")) as JSONObject
        var rankedJson = responseJson["competitiveStats"] as JSONObject
        var normalJson = responseJson["quickPlayStats"] as JSONObject
        var playRankedGames = rankedJson["games"] as JSONObject
        var playNormalGames = normalJson["games"] as JSONObject
        var rankedCareerStats = rankedJson["careerStats"] as JSONObject
        var normalCareerStats = normalJson["careerStats"] as JSONObject
        var rankedAllHeroes = rankedCareerStats["allHeroes"] as JSONObject
        var normalAllHeroes = normalCareerStats["allHeroes"] as JSONObject
        var rankedCombat = rankedAllHeroes["combat"] as JSONObject
        var normalCombat = normalAllHeroes["combat"] as JSONObject
        var ratings = responseJson["ratings"] as JSONArray

        var i = ratings.size

        var rankedHero = Hero(
            0,
            responseJson["name"] as String,
            battletag,
            Type.RANKED,
            wins = playRankedGames["won"] as Long,
            losses = (playRankedGames["played"] as Long) - (playRankedGames["won"] as Long),
            kills = rankedCombat["eliminations"] as Long,
            deaths = rankedCombat["deaths"] as Long
        )
        //position별 tier 분류
        for(j in 0 until i) {
            var rating = ratings[j] as JSONObject
            var role = rating["role"] as String
            when(role){
                "tank" -> {
                    rankedHero.tank_tier = rating["group"].toString()
                    rankedHero.tank_rank = rating["tier"].toString()
                }
                "offense" -> {
                    rankedHero.damage_tier = rating["group"].toString()
                    rankedHero.damage_rank = rating["tier"].toString()
                }
                "support" -> {
                    rankedHero.support_tier = rating["group"].toString()
                    rankedHero.support_rank = rating["tier"].toString()
                }
                else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
            }
        }
        //most Hero 추출 by ranked
        mostHeroList = getMostHeroes(rankedCareerStats)
        rankedHero.most1Hero = mostHeroList[0].first
        rankedHero.most2Hero = mostHeroList[1].first
        rankedHero.most3Hero = mostHeroList[2].first

        heroRepository.save(rankedHero)

        var normalHero = rankedHero
        normalHero.wins = playNormalGames["won"] as Long
        normalHero.losses = (playNormalGames["played"] as Long) - normalHero.wins
        normalHero.kills = normalCombat["eliminations"] as Long
        normalHero.deaths = normalCombat["deaths"] as Long

        //most Hero 추출 by normal
        mostHeroList = getMostHeroes(normalCareerStats)
        normalHero.most1Hero = mostHeroList[0].first
        normalHero.most2Hero = mostHeroList[1].first
        normalHero.most3Hero = mostHeroList[2].first

        heroRepository.save(normalHero)
    }

    fun getMostHeroes(careerStats: JSONObject): List<Pair<String, String>> {
        var returnData = mutableListOf<Pair<String, String>>()
        var pair: Pair<String, String>
        var hero: JSONObject
        var game: JSONObject
        var time: String

        careerStats.keys.forEach { i ->
            hero = careerStats[i] as JSONObject
            game = hero["game"] as JSONObject
            time = game["timePlayed"] as String
            pair = Pair(i.toString(), time)
            returnData.add(pair)
        }
        returnData.toList().sortedByDescending { it.second }

        return returnData
    }

    fun getHeroIsExist(name: String, battletag: Long): Boolean {
        val request = HttpGet("$serverUrl$name-$battletag/complete")
        val battleNetUser = HttpClientBuilder.create().build().execute(request)
        if(battleNetUser.statusLine.statusCode == 404){
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        if(battleNetUser.statusLine.statusCode != 200){
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        return true
    }

    fun getMemberList(boardId: Long): List<String>{
        val board = overwatchRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomId(board.get().chatRoomId)
        var memberList = mutableListOf<String>()
        for(element in chatRooms){
            if(element.oauth2Id == "banned")   continue
            element.nickname?.let { memberList.add(it) }
        }
        return memberList
    }

    fun getBanList(boardId: Long): List<String>{
        val board = overwatchRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomIdAndOauth2Id(board.get().chatRoomId, "banned")
        var banList = mutableListOf<String>()
        for(element in chatRooms){
            element.nickname?.let { banList.add(it) }
        }
        return banList
    }

    @Transactional
    fun updateExpired(){
        val boards = overwatchRepository.findAll()
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

    @Transactional
    fun deleteOldHero(name: String, battletag: Long){
        if(heroRepository.existsByNameAndBattletag(name, battletag)){
            heroRepository.deleteByNameAndBattletag(name, battletag)
        }
    }
}