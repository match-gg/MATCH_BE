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
import gg.match.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ChatService (
    private val userRepository: UserRepository,
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
                if(user.lol?.let { validateMemberByChatRoom(game, board.chatRoomId, it) } == true){
                    throw BusinessException(ErrorCode.USER_DUPLICATED)
                }
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
    fun addMember(game: Game, id: Long, nickname: String){
        when(game){
            Game.valueOf("LOL") -> {
                val board = loLRepository.findByIdOrNull(id)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
                if(validateMemberByChatRoom(game, board.chatRoomId, nickname))
                    throw BusinessException(ErrorCode.USER_DUPLICATED)
                ChatRoomDTO(board.chatRoomId, nickname, "guest").toEntity()
                    ?.let { chatRepository.save(it) }
                board.nowUser += 1
                loLRepository.save(board)
                return
            }
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    @Transactional
    fun decreaseMember(game: Game, id: Long, oauth2Id: String){
        var nickname: String
        when(game){
            Game.valueOf("LOL") -> {
                val board = loLRepository.findByIdOrNull(id)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
                nickname = if("kakao" in oauth2Id){
                    var user = userRepository.findByOauth2Id(oauth2Id) ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
                    user.lol.toString()
                } else oauth2Id
                if(!validateMemberByChatRoom(game, board.chatRoomId, nickname))
                    throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
                chatRepository.deleteByChatRoomIdAndNickname(board.chatRoomId, nickname)
                board.nowUser -= 1
                loLRepository.save(board)
                return
            }
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    @Transactional
    fun banMember(game: Game, id: Long, nickname: String){
        when(game){
            Game.valueOf("LOL") -> {
                val board = loLRepository.findByIdOrNull(id)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
                var user = chatRepository.findByChatRoomIdAndAndNickname(board.chatRoomId, nickname)
                user.update(ChatRoomDTO(user.chatRoomId, user.nickname, user.oauth2Id))
            }
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    fun validateMemberByChatRoom(game: Game, chatRoomId: String, nickname: String): Boolean{
        when(game){
            Game.valueOf("LOL") -> {
                return chatRepository.existsByChatRoomIdAndNickname(chatRoomId, nickname)
            }
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }
}
