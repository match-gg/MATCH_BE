package gg.match.domain.user.service

import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.user.dto.FollowerReturnDTO
import gg.match.domain.user.dto.FollowerReturnWrapDTO
import gg.match.domain.user.dto.LikeRequestDTO
import gg.match.domain.user.dto.UserPlayInfoDTO
import gg.match.domain.user.entity.Follow
import gg.match.domain.user.entity.Game
import gg.match.domain.user.entity.User
import gg.match.domain.user.repository.FollowRepository
import gg.match.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService (
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) {
    @Transactional
    fun saveUser(user: User) = userRepository.save(user)

    fun existsByOauth2Id(oauth2Id: String)
    = userRepository.existsByOauth2Id(oauth2Id)

    fun findByOauth2Id(oauth2Id: String)
    = userRepository.findByOauth2Id(oauth2Id)

    @Transactional
    fun increaseLike(likeRequestDTO: LikeRequestDTO): Long?{
        val user = getUserByGame(likeRequestDTO)
        return if(user == null) null
        else{
            user.likeCount += 1
            user.id
        }
    }

    @Transactional
    fun increaseDislike(likeRequestDTO: LikeRequestDTO): Long?{
        val user = getUserByGame(likeRequestDTO)
        return if(user == null) null
        else{
            user.dislikeCount += 1
            user.id
        }
    }

    fun getUserByGame(likeRequestDTO: LikeRequestDTO): User?{
        val user: User = when(Game.valueOf(likeRequestDTO.game.uppercase())){
            Game.LOL -> userRepository.findByLol(likeRequestDTO.nickname)
            Game.PUBG -> userRepository.findByPubg(likeRequestDTO.nickname)
            Game.OVERWATCH -> userRepository.findByOverwatch(likeRequestDTO.nickname)
            Game.VALORANT -> userRepository.findByValorant(likeRequestDTO.nickname)
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        } ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        return user
    }

    @Transactional
    fun changeNickname(game: Game, nickname: String, user: User){
        val userData = userRepository.findByOauth2Id(user.oauth2Id)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        when(game){
            Game.LOL -> userData.lol = nickname
            Game.OVERWATCH -> userData.overwatch = nickname
            Game.PUBG -> userData.pubg = nickname
            Game.VALORANT -> userData.valorant = nickname
            else -> throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    @Transactional
    fun changeRepresentative(game: Game, user: User){
        val userData = userRepository.findByOauth2Id(user.oauth2Id)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        userData.representative = game
    }

    fun getUserPlayInfo(oauth2Id: String): UserPlayInfoDTO{
        val user = userRepository.findByOauth2Id(oauth2Id)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        return UserPlayInfoDTO(user.matchCount, user.likeCount, user.dislikeCount)
    }

    @Transactional
    fun following(user: User, oauth2Id: String){
        if(!followRepository.existsByOauth2IdAndFollowing(user.oauth2Id, oauth2Id)) {
            val savedFollow = Follow(
                oauth2Id = user.oauth2Id,
                following = oauth2Id
            )
            followRepository.save(savedFollow)
        }
        else    throw BusinessException(ErrorCode.INVALID_FOLLOWER)
    }

    @Transactional
    fun cancelFollowing(user: User, oauth2Id: String){
        val follow = followRepository.findByOauth2IdAndFollowing(user.oauth2Id, oauth2Id)
            ?: throw BusinessException(ErrorCode.FOLLOWERS_NOT_FOUND)
        followRepository.delete(follow)
    }

    fun getFollower(user: User): FollowerReturnWrapDTO{
        var follower: User?
        val followList = followRepository.findAllByOauth2Id(user.oauth2Id)
        val followerList = mutableListOf<FollowerReturnDTO>()
        for(element in followList){
            follower = userRepository.findByOauth2Id(element.following)
                ?: throw BusinessException(ErrorCode.FOLLOWERS_NOT_FOUND)
            follower.let { followerList.add(FollowerReturnDTO(it.oauth2Id, it.lol, it.pubg, it.overwatch, it.valorant)) }
        }
        return FollowerReturnWrapDTO(followerList)
    }
}