package gg.match.domain.user.service

import gg.match.common.jwt.service.JwtService
import gg.match.common.util.Constants.Companion.REFRESH_TOKEN_PREFIX
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.user.dto.*
import gg.match.domain.user.entity.User
import gg.match.domain.user.oauth.OAuth2ServiceFactory
import gg.match.domain.user.service.refresh.RefreshService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userService: UserService,
    private val oAuth2ServiceFactory: OAuth2ServiceFactory,
    private val jwtService: JwtService,
    private val refreshService: RefreshService
) {
    @Transactional
    fun signUp(signUpRequestDTO: SignUpRequestDTO): JwtTokenDTO{
        val oAuth2User = oAuth2ServiceFactory
            .getOAuthService()
            .getOAuth2User(signUpRequestDTO.oauth2AccessToken)

        if(userService.existsByOauth2Id(oAuth2User.oauth2Id)) {
            throw BusinessException(ErrorCode.USER_DUPLICATED)
        }

        userService.saveUser(User.of(oAuth2User, signUpRequestDTO))

        val jwtToken = jwtService.issue(signUpRequestDTO.oauth2AccessToken)
        refreshService.storeRefresh(jwtToken, oAuth2User.oauth2Id)

        return jwtToken
    }

    @Transactional
    fun signin(signInRequestDTO: SignInRequestDTO): JwtTokenDTO {
        val jwtToken = jwtService.issue(signInRequestDTO.oauth2AccessToken)
        val oAuth2User = oAuth2ServiceFactory
            .getOAuthService()
            .getOAuth2User(signInRequestDTO.oauth2AccessToken)

        refreshService.storeRefresh(jwtToken, oAuth2User.oauth2Id)
        return jwtToken
    }

    @Transactional
    fun refresh(refreshToken: String): JwtTokenDTO {
        val oAuth2Id = refreshService.getRefresh("$REFRESH_TOKEN_PREFIX:${refreshToken}")
            ?: throw BusinessException(ErrorCode.INVALID_JWT)

        val user: User = userService.findByOauth2Id(oAuth2Id)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        val jwtToken = jwtService.refresh(refreshToken, user.oauth2Id)

        refreshService.storeRefresh(jwtToken, oAuth2Id)
        return jwtToken
    }

    @Transactional
    fun logout(refreshToken: String, accessToken: String){
        refreshService.deleteRefresh("$REFRESH_TOKEN_PREFIX:${refreshToken}")
    }
}