package gg.match.domain.board.lol.service

import com.fasterxml.jackson.databind.ObjectMapper
import gg.match.controller.common.dto.PageResult
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.lol.repository.LoLRepository
import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.dto.ReadLoLBoardDTO
import gg.match.domain.board.lol.dto.SummonerReadDTO
import gg.match.domain.board.lol.entity.Position
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type
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

    fun getBoards(pageable: Pageable, position: Position, type: Type, tier: Tier): PageResult<ReadLoLBoardDTO> {
        val boards = if(type == Type.valueOf("ALL")){
            loLRepository.findByPositionAndTier(pageable, position, tier)
        } else{
            loLRepository.findByPositionAndTypeAndTier(pageable, position, type, tier)
        }
        return PageResult.ok(boards.map { it.toReadLoLBoardDTO() })
    }

    fun getBoard(boardId: Long): ReadLoLBoardDTO {
        val board = loLRepository.findById(boardId)
        return board.get().toReadLoLBoardDTO()
    }

    @Transactional
    fun save(loLRequestDTO: LoLRequestDTO): Long? {
        val board = loLRepository.save(loLRequestDTO.toEntity())
        return board.id
    }

    @Transactional
    fun update(boardId: Long, loLRequestDTO: LoLRequestDTO): ReadLoLBoardDTO {
        val board = loLRepository.findByIdOrNull(boardId)
            ?: throw Exception("not found")

        board.update(loLRequestDTO)
        return board.toReadLoLBoardDTO()
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
            try {
                var request = HttpGet("$serverUrl/lol/league/v4/entries/by-summoner/$responseUser?api_key=$lolApiKey")
                var responseSummoner: HttpResponse = HttpClientBuilder.create().build().execute(request)
                var userJson = parser.parse(EntityUtils.toString(responseSummoner.entity, "UTF-8")) as JSONArray
                if(userJson.isEmpty()){
                    return
                }
                else{
                    for(i in 0 until userJson.size){
                        summonerRepository.save(objectMapper.readValue(userJson[i].toString(), SummonerReadDTO::class.java).toEntity())
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun getUserInfoByRiotApi(nickname: String): Any? {
        return try {
            var request = HttpGet("$serverUrl/lol/summoner/v4/summoners/by-name/$nickname?api_key=$lolApiKey")
            var riotUser = HttpClientBuilder.create().build().execute(request)
            if(riotUser.statusLine.statusCode == 404){
                println("여기옴")
                throw BusinessException(ErrorCode.USER_NOT_FOUND)
            }
            if(riotUser.statusLine.statusCode != 200){
                throw BusinessException(ErrorCode.USER_NOT_RANKED)
            }
            var riotUserJson = parser.parse(EntityUtils.toString(riotUser.entity, "UTF-8")) as JSONObject
            riotUserJson["id"]
        } catch (e: Exception) {
            e.printStackTrace()
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }
}