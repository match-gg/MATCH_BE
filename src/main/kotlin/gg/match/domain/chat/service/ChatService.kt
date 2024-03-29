package gg.match.domain.chat.service

import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.lol.entity.LoL
import gg.match.domain.board.lol.repository.LoLRepository
import gg.match.domain.board.overwatch.entity.Overwatch
import gg.match.domain.board.overwatch.repository.OverwatchRepository
import gg.match.domain.board.pubg.entity.Pubg
import gg.match.domain.board.pubg.repository.PubgRepository
import gg.match.domain.board.valorant.entity.Valorant
import gg.match.domain.board.valorant.repository.ValorantRepository
import gg.match.domain.chat.dto.ChatRoomDTO
import gg.match.domain.chat.dto.ChatRoomListDTO
import gg.match.domain.chat.dto.ChatRoomRequestDTO
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
    private val loLRepository: LoLRepository,
    private val pubgRepository: PubgRepository,
    private val valorantRepository: ValorantRepository,
    private val overwatchRepository: OverwatchRepository
){
    @Transactional
    fun saveChatRoomId(game: Game, chatRoomRequestDTO: ChatRoomRequestDTO, user: User): Any{
        val board = when(game){
            Game.LOL -> getBoardByGame(game, chatRoomRequestDTO.boardId) as LoL
            Game.PUBG -> getBoardByGame(game, chatRoomRequestDTO.boardId) as Pubg
            Game.OVERWATCH -> getBoardByGame(game, chatRoomRequestDTO.boardId) as Overwatch
            Game.VALORANT -> getBoardByGame(game, chatRoomRequestDTO.boardId) as Valorant
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }

        board.update(chatRoomRequestDTO.chatRoomId, chatRoomRequestDTO.totalUser)
        ChatRoomDTO(chatRoomRequestDTO.chatRoomId, board.name, user.oauth2Id).toEntity()
            ?.let { chatRepository.save(it) }
        return board
    }

    fun getChatRooms(user: User): ChatRoomListDTO{
        return ChatRoomListDTO(chatRepository.findAllByOauth2Id(user.oauth2Id))
    }

    @Transactional
    fun increaseMember(game: Game, id: Long, user: User){
        checkIncreaseMemberCount(game, id)
        val board = when(game){
            Game.LOL -> getBoardByGame(game, id) as LoL
            Game.PUBG -> getBoardByGame(game, id) as Pubg
            Game.OVERWATCH -> getBoardByGame(game, id) as Overwatch
            Game.VALORANT -> getBoardByGame(game, id) as Valorant
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        val nickname = when(game){
            Game.LOL -> user.lol
            Game.PUBG -> user.pubg
            Game.OVERWATCH -> user.overwatch
            Game.VALORANT -> user.valorant
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        if(nickname?.let { validateMemberByChatRoom(game, board.chatRoomId, it) } == true){
            throw BusinessException(ErrorCode.BAD_REQUEST)
        }
        board.nowUser += 1
        when(game){
            Game.LOL -> loLRepository.save(board as LoL)
            Game.PUBG -> pubgRepository.save(board as Pubg)
            Game.OVERWATCH -> overwatchRepository.save(board as Overwatch)
            Game.VALORANT -> valorantRepository.save(board as Valorant)
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        ChatRoomDTO(board.chatRoomId, nickname, user.oauth2Id).toEntity()
            ?.let { chatRepository.save(it) }
        return
    }

    @Transactional
    fun addMember(game: Game, id: Long, nickname: String){
        checkIncreaseMemberCount(game, id)
        val board = when(game){
            Game.LOL -> getBoardByGame(game, id) as LoL
            Game.PUBG -> getBoardByGame(game, id) as Pubg
            Game.OVERWATCH -> getBoardByGame(game, id) as Overwatch
            Game.VALORANT -> getBoardByGame(game, id) as Valorant
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        if(validateMemberByChatRoom(game, board.chatRoomId, nickname))
            throw BusinessException(ErrorCode.BAD_REQUEST)
        ChatRoomDTO(board.chatRoomId, nickname, "guest$nickname").toEntity()
            ?.let { chatRepository.save(it) }
        board.nowUser += 1
        when(game){
            Game.LOL -> loLRepository.save(board as LoL)
            Game.PUBG -> pubgRepository.save(board as Pubg)
            Game.OVERWATCH -> overwatchRepository.save(board as Overwatch)
            Game.VALORANT -> valorantRepository.save(board as Valorant)
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        return
    }

    @Transactional
    fun decreaseMember(game: Game, id: Long, oauth2Id: String){
        checkDecreaseMemberCount(game, id)
        val board = when(game){
            Game.LOL -> getBoardByGame(game, id) as LoL
            Game.PUBG -> getBoardByGame(game, id) as Pubg
            Game.OVERWATCH -> getBoardByGame(game, id) as Overwatch
            Game.VALORANT -> getBoardByGame(game, id) as Valorant
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        val nickname: String = if("kakao" in oauth2Id){
            val user = userRepository.findByOauth2Id(oauth2Id) ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
            when(game){
                Game.LOL -> user.lol
                Game.PUBG -> user.pubg
                Game.OVERWATCH -> user.overwatch
                Game.VALORANT -> user.valorant
                else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
            }
        } else oauth2Id
        if(!validateMemberByChatRoom(game, board.chatRoomId, nickname))
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        chatRepository.deleteByChatRoomIdAndNickname(board.chatRoomId, nickname)
        board.nowUser -= 1
        when(game){
            Game.LOL -> loLRepository.save(board as LoL)
            Game.PUBG -> pubgRepository.save(board as Pubg)
            Game.OVERWATCH -> overwatchRepository.save(board as Overwatch)
            Game.VALORANT -> valorantRepository.save(board as Valorant)
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        return
    }

    @Transactional
    fun banMember(game: Game, id: Long, nickname: String){
        checkDecreaseMemberCount(game, id)
        val board = when(game){
            Game.LOL -> getBoardByGame(game, id) as LoL
            Game.PUBG -> getBoardByGame(game, id) as Pubg
            Game.OVERWATCH -> getBoardByGame(game, id) as Overwatch
            Game.VALORANT -> getBoardByGame(game, id) as Valorant
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        board.nowUser -= 1
        val user = chatRepository.findByChatRoomIdAndNickname(board.chatRoomId, nickname)
        user.update(ChatRoomDTO(user.chatRoomId, user.nickname, user.oauth2Id))
    }

    @Transactional
    fun startGame(game: Game, id: Long){
        val board = when(game){
            Game.LOL -> getBoardByGame(game, id) as LoL
            Game.PUBG -> getBoardByGame(game, id) as Pubg
            Game.OVERWATCH -> getBoardByGame(game, id) as Overwatch
            Game.VALORANT -> getBoardByGame(game, id) as Valorant
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        val chatRoomId = board.chatRoomId
        val chatRoomList = chatRepository.findAllByChatRoomId(chatRoomId)
        chatRoomList.forEach { i ->
            val inGameUser = userRepository.findByOauth2Id(i.oauth2Id)
                ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
            inGameUser.matchCount += 1
        }
        board.finished = "true"
        board.update("true")
    }

    @Transactional
    fun changeTotalUser(game: Game, id: Long, totalUser: Int){
        val board = when(game){
            Game.LOL -> getBoardByGame(game, id) as LoL
            Game.PUBG -> getBoardByGame(game, id) as Pubg
            Game.OVERWATCH -> getBoardByGame(game, id) as Overwatch
            Game.VALORANT -> getBoardByGame(game, id) as Valorant
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        board.totalUser = totalUser
    }

    fun validateMemberByChatRoom(game: Game, chatRoomId: String, nickname: String): Boolean{
        return chatRepository.existsByChatRoomIdAndNickname(chatRoomId, nickname)
    }

    fun checkIncreaseMemberCount(game: Game, id: Long) {
        val board = when(game){
            Game.LOL -> getBoardByGame(game, id) as LoL
            Game.PUBG -> getBoardByGame(game, id) as Pubg
            Game.OVERWATCH -> getBoardByGame(game, id) as Overwatch
            Game.VALORANT -> getBoardByGame(game, id) as Valorant
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        if(board.totalUser == board.nowUser) {
            throw BusinessException(ErrorCode.MEMBER)
        }
    }

    fun checkDecreaseMemberCount(game: Game, id: Long) {
        val board = when(game){
            Game.LOL -> getBoardByGame(game, id) as LoL
            Game.PUBG -> getBoardByGame(game, id) as Pubg
            Game.OVERWATCH -> getBoardByGame(game, id) as Overwatch
            Game.VALORANT -> getBoardByGame(game, id) as Valorant
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        if(board.nowUser == 0) {
            throw BusinessException(ErrorCode.MEMBER)
        }
    }

    fun getBoardByGame(game: Game, id: Long): Any{
        return when(game){
            Game.LOL -> {
                loLRepository.findByIdOrNull(id)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
            }
            Game.PUBG -> {
                pubgRepository.findByIdOrNull(id)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
            }
            Game.OVERWATCH -> {
                overwatchRepository.findByIdOrNull(id)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
            }
            Game.VALORANT -> {
                valorantRepository.findByIdOrNull(id)
                    ?: throw BusinessException(ErrorCode.NO_BOARD_FOUND)
            }
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }
}
