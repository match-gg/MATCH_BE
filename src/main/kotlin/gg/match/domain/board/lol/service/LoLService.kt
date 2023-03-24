package gg.match.domain.board.lol.service

import com.fasterxml.jackson.databind.ObjectMapper
import gg.match.controller.common.dto.PageResult
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.lol.repository.LoLRepository
import gg.match.domain.board.lol.dto.LoLRequestDTO
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

@Service
@Transactional(readOnly = true)
class LoLService(
    @Value("\${lol.mykey}") private val lolApiKey: String,
    private val loLRepository: LoLRepository,
    private val summonerRepository: SummonerRepository,
    private val objectMapper: ObjectMapper
) {
    private val serverUrl = "https://kr.api.riotgames.com"
    val parser = JSONParser()
    var summonerName: String = "수유욱"
    lateinit var summoner: Summoner
    lateinit var result: PageResult<ReadLoLBoardDTO>

    fun getBoards(pageable: Pageable, position: Position, type: Type, tier: Tier): PageResult<ReadLoLBoardDTO> {
        val boards = if(type == Type.valueOf("ALL")){
            loLRepository.findByPositionAndTier(pageable, position, tier)
        } else{
            loLRepository.findByPositionAndTypeAndTier(pageable, position, type, tier)
        }
        if(boards.isEmpty) throw BusinessException(ErrorCode.NO_BOARD_FOUND)

        //init <-관리자로 초기화
        summoner = summonerRepository.findBySummonerNameAndQueueType(summonerName, "RANKED_SOLO_5x5")
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
        }
    }

    fun getUserInfoByRiotApi(nickname: String): Any? {
        val request = HttpGet("$serverUrl/lol/summoner/v4/summoners/by-name/$nickname?api_key=$lolApiKey")
        val riotUser = HttpClientBuilder.create().build().execute(request)
        if(riotUser.statusLine.statusCode == 404){
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        if(riotUser.statusLine.statusCode != 200){
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        val riotUserJson = parser.parse(EntityUtils.toString(riotUser.entity, "UTF-8")) as JSONObject
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
}