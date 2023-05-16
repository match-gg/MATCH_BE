package gg.match.domain.chat.service

import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.lol.repository.LoLRepository
import gg.match.domain.chat.dto.ChatRoomDTO
import gg.match.domain.chat.dto.ChatRoomListDTO
import gg.match.domain.chat.dto.ChatRoomRequestDTO
import gg.match.domain.chat.entity.ChatRoom
import gg.match.domain.chat.repository.ChatRepository
import gg.match.domain.user.entity.Game
import gg.match.domain.user.entity.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatService (
    private val chatRepository: ChatRepository,
    private val loLRepository: LoLRepository
){
    @Transactional
    fun saveChatRoomId(game: Game, chatRoomRequestDTO: ChatRoomRequestDTO, user: User): Any{
        when(game){
            Game.valueOf("LOL") -> {
                val board = loLRepository.findByIdOrNull(chatRoomRequestDTO.boardId)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)

                board.update(chatRoomRequestDTO.chatRoomId, chatRoomRequestDTO.totalUser)
                ChatRoomDTO(chatRoomRequestDTO.chatRoomId, user.lol, user.oauth2Id).toEntity()
                    ?.let { chatRepository.save(it) }
                return board
            }
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    fun getChatRooms(user: User): ChatRoomListDTO{
        return ChatRoomListDTO(chatRepository.findAllByOauth2Id(user.oauth2Id))
    }

    @Transactional
    fun increaseMember(game: Game, id: Long, user: User){
        when(game){
            Game.valueOf("LOL") -> {
                val board = loLRepository.findByIdOrNull(id)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
                ChatRoomDTO(board.chatRoomId, user.lol, user.oauth2Id).toEntity()
                    ?.let { chatRepository.save(it) }
                board.nowUser += 1
                loLRepository.save(board)
                return
            }
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    @Transactional
    fun decreaseMember(game: Game, id: Long, user: User){
        when(game){
            Game.valueOf("LOL") -> {
                val board = loLRepository.findByIdOrNull(id)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
                val chatRoom = chatRepository.findByChatRoomId(board.chatRoomId)
                chatRoom.toEntity()?.let { chatRepository.delete(it) }
                board.nowUser -= 1
                loLRepository.save(board)
                return
            }
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    @Transactional
    fun kickMember(game: Game, id: Long, oauth2Id: String){
        when(game){
            Game.valueOf("LOL") -> {
                val board = loLRepository.findByIdOrNull(id)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
                val chatRoom = chatRepository.findByChatRoomIdAndOauth2Id(board.chatRoomId, oauth2Id)
                chatRoom.toEntity()?.let { chatRepository.delete(it) }
                board.nowUser -= 1
                loLRepository.save(board)
                return
            }
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }
}
