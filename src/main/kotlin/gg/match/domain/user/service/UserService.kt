package gg.match.domain.user.service

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
}