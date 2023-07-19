package gg.match.domain.user.service

import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.user.dto.LikeRequestDTO
import gg.match.domain.user.entity.Game
import gg.match.domain.user.entity.User
import gg.match.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService (
    private val userRepository: UserRepository
) {
    @Transactional
    fun saveUser(user: User) = userRepository.save(user)

    fun existsByOauth2Id(oauth2Id: String)
    = userRepository.existsByOauth2Id(oauth2Id)

    fun findByOauth2Id(oauth2Id: String)
    = userRepository.findByOauth2Id(oauth2Id)

    @Transactional
    fun finishedBoard(): String{
        return ""
    }

    @Transactional
    fun increaseLike(likeRequestDTO: LikeRequestDTO): Long?{
        var user = getUserByGame(likeRequestDTO)
        return if(user == null) null
        else{
            user.likeCount += 1
            user.id
        }
    }

    @Transactional
    fun increaseDislike(likeRequestDTO: LikeRequestDTO): Long?{
        var user = getUserByGame(likeRequestDTO)
        return if(user == null) null
        else{
            user.dislikeCount += 1
            user.id
        }
    }

    fun getUserByGame(likeRequestDTO: LikeRequestDTO): User?{
        var user: User? = when(Game.valueOf(likeRequestDTO.game.uppercase())){
            Game.LOL -> userRepository.findByLol(likeRequestDTO.nickname)
            Game.PUBG -> userRepository.findByPubg(likeRequestDTO.nickname)
            Game.OVERWATCH -> userRepository.findByOverwatch(likeRequestDTO.nickname)
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        } ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        return user
    }
}