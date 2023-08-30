package gg.match.common.jwt.service

import gg.match.common.dto.AdminLoginDTO
import gg.match.common.jwt.util.JwtProvider
import gg.match.common.jwt.util.JwtResolver
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.user.dto.JwtTokenDTO
import gg.match.domain.user.entity.User
import gg.match.domain.user.oauth.OAuth2ServiceFactory
import gg.match.domain.user.service.UserService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val jwtProvider: JwtProvider,
    private val jwtResolver: JwtResolver,
    private val userService: UserService,
    private val oAuth2ServiceFactory: OAuth2ServiceFactory,
    private val encoder: PasswordEncoder
) {
    fun adminIssue(adminLoginDTO: AdminLoginDTO): JwtTokenDTO{
        val user: User = userService.findByOauth2Id(adminLoginDTO.id)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        if(encoder.matches(adminLoginDTO.pw, user.password)) {
            val accessToken = jwtProvider.createAccessToken(user.oauth2Id)
            val refreshToken = jwtProvider.createRefreshToken()
            return JwtTokenDTO(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }
        else{
            throw BusinessException(ErrorCode.INVALID_PW)
        }
    }

    fun issue(oauth2AccessToken: String): JwtTokenDTO {
        val oAuth2Id: String = oAuth2ServiceFactory
            .getOAuthService()
            .getOAuth2User(oauth2AccessToken)
            .oauth2Id

        val user: User = userService.findByOauth2Id(oAuth2Id)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        val accessToken: String = jwtProvider.createAccessToken(user.oauth2Id)
        val refreshToken: String = jwtProvider.createRefreshToken()

        return JwtTokenDTO(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun refresh(refreshToken: String, oauth2Id: String): JwtTokenDTO {
        var localRefreshToken = refreshToken
        if(isValidate(refreshToken)) {
            throw BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN)
        }
        val accessToken = jwtProvider.createAccessToken(oauth2Id)

        if(isRefreshable(refreshToken)) {
            localRefreshToken = jwtProvider.createRefreshToken()
        }
        return JwtTokenDTO(accessToken, localRefreshToken)
    }

    fun getRemainExpiry(token: String): Long {
        val expiration = jwtResolver.parseToken(token).body.expiration
        val now = Date()
        return expiration.time - now.time
    }

    private fun isValidate(refreshToken: String): Boolean {
        val now = Date()
        return !jwtResolver.isExpired(refreshToken, now)
    }

    private fun isRefreshable(refreshToken: String): Boolean {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA)
        calendar.time = Date()
        calendar.add(Calendar.DATE, 3)
        return !jwtResolver.isExpired(refreshToken, calendar.time)
    }
}