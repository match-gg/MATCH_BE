package gg.match.domain.board.valorant.service

import gg.match.common.dto.ChatMemberListDTO
import gg.match.controller.common.dto.PageResult
import gg.match.controller.common.entity.Expire
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.overwatch.dto.ReadOverwatchBoardDTO
import gg.match.domain.board.valorant.dto.AgentResponseDTO
import gg.match.domain.board.valorant.dto.ReadValorantBoardDTO
import gg.match.domain.board.valorant.dto.ValorantRequestDTO
import gg.match.domain.board.valorant.entity.Agent
import gg.match.domain.board.valorant.entity.Valorant
import gg.match.domain.board.valorant.entity.ValorantGameModes
import gg.match.domain.board.valorant.repository.AgentByMatchRepository
import gg.match.domain.board.valorant.repository.AgentRepository
import gg.match.domain.board.valorant.repository.ValorantRepository
import gg.match.domain.chat.repository.ChatRepository
import gg.match.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional(readOnly = true)
class ValorantBoardService (
    private val valorantRepository: ValorantRepository,
    private val agentRepository: AgentRepository,
    private val chatRepository: ChatRepository
){
    lateinit var result: PageResult<ReadValorantBoardDTO>

    @Transactional
    fun getFollowerBoards(user: User, pageable: Pageable, oauth2Ids: List<String>): PageResult<ReadValorantBoardDTO>{
        val boards = valorantRepository.findAllByOauth2IdInAndExpiredAndFinishedOrderByIdDesc(pageable, oauth2Ids, "false", "false")
        if(boards.isEmpty) throw BusinessException(ErrorCode.NO_BOARD_FOUND)
        val result = PageResult.ok(boards.map { it.toReadValorantBoardDTO(agentRepository.findByNameAndGameMode(it.name, it.valorantGameModes.toString()).toAgentResponseDTO(), getMemberList(it.id, "false"), getMemberList(it.id, "true"))})
        for(i in 0 until boards.content.size){
            val name = boards.content[i].name
            result.content[i].author = getAgentByName(name, boards.content[i].valorantGameModes)
        }
        return result
    }

    fun getBoards(pageable: Pageable, gameMode: ValorantGameModes): PageResult<ReadValorantBoardDTO> {
        val boards = if(gameMode == ValorantGameModes.ALL){
            valorantRepository.findAllByOrderByExpiredAscIdDesc(pageable)
        } else{
            valorantRepository.findAllByValorantGameModesOrderByExpiredAscIdDesc(pageable, gameMode.toString())
        }
        //update expired
        updateExpired()
        //get boards
        if(boards.isEmpty)  throw BusinessException(ErrorCode.NO_BOARD_FOUND)
        result = PageResult.ok(boards.map { it.toReadValorantBoardDTO(agentRepository.findByNameAndGameMode(it.name, it.valorantGameModes.toString()).toAgentResponseDTO(), getMemberList(it.id, "false"), getMemberList(it.id, "true"))})

        for(i in 0 until boards.content.size){
            val playerName = boards.content[i].name
            result.content[i].author = getAgentByName(playerName, boards.content[i].valorantGameModes)
        }
        return result
    }

    fun getBoard(boardId: Long): ReadValorantBoardDTO {
        val board = getBoardByBoardId(boardId).get()
        return board.toReadValorantBoardDTO(agentRepository.findByNameAndGameMode(board.name, board.valorantGameModes.toString()).toAgentResponseDTO(), getMemberList(boardId, "false"), getMemberList(boardId, "true"))
    }

    fun save(valorantRequestDTO: ValorantRequestDTO, user: User): Long?{
        val board = valorantRepository.save(valorantRequestDTO.toEntity(user.oauth2Id))
        board.name = valorantRequestDTO.name
        return board.id
    }

    fun update(boardId: Long, valorantRequestDTO: ValorantRequestDTO): Valorant {
        val board = valorantRepository.findByIdOrNull(boardId)?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
        board.update(valorantRequestDTO)
        return board
    }

    fun delete(boardId: Long){
        val board = valorantRepository.findByIdOrNull(boardId)?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
        val chat = chatRepository.findAllByChatRoomId(board.chatRoomId)
        for(element in chat)
            chatRepository.delete(element)
    }

    private fun getAgentByName(name: String, valorantGameModes: String): AgentResponseDTO {
        return try{
            agentRepository.findByNameAndGameMode(name, valorantGameModes).toAgentResponseDTO()
        } catch (e: Exception){
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
    }

    @Transactional
    fun updateExpired(){
        val boards = valorantRepository.findAll()
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

    fun getMemberList(boardId: Long, isBanned: String): List<ChatMemberListDTO>{
        val board = getBoardByBoardId(boardId)
        val chatRooms = chatRepository.findAllByChatRoomIdAndIsBanned(board.get().chatRoomId, isBanned)
        val memberList = mutableListOf<ChatMemberListDTO>()
        for(element in chatRooms){
            element.nickname?.let { memberList.add(ChatMemberListDTO(element.oauth2Id, it)) }
        }
        return memberList
    }

    fun getBoardByBoardId(boardId: Long): Optional<Valorant> {
        return valorantRepository.findById(boardId)
    }
}