package gg.match.domain.user.service

import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.user.dto.RegisterDTO
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
    fun register(registerDTO: RegisterDTO, user: User) {
        val oldUser: User = userRepository.findByOauth2Id(user.oauth2Id)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        userRepository.save(oldUser.register(registerDTO))
    }
}