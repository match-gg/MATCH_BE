package gg.match.domain.user.service

import gg.match.domain.user.dto.*
import gg.match.domain.user.entity.User
import gg.match.domain.user.oauth.OAuthServiceFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userService: UserService,
    private val oAuthServiceFactory: OAuthServiceFactory
) {
    @Transactional
    fun signUp(signUpRequestDTO: SignUpRequestDTO): SignUpResponseDTO{
        val oAuth2User = oAuthServiceFactory
            .getOAuthService()
            .getOAuth2User(signUpRequestDTO.oauth2AccessToken)
        if(userService.existsByOauth2Id(oAuth2User.oauth2Id)) {
            throw Exception("Error")
        }

        val savedUser: User = userService.saveUser(
            User.of(oAuth2User)
        )

        return SignUpResponseDTO(
            oauth2Id =  savedUser.oauth2Id
        )
    }
}