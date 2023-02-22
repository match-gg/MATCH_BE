package gg.match.common.jwt.user

import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Repository

@Repository
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        return UserDetailsImpl(userRepository.findByOauth2Id(username)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        )
    }
}