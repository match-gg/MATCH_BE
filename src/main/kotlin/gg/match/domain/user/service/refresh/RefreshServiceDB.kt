package gg.match.domain.user.service.refresh

import gg.match.common.util.Constants.Companion.REFRESH_TOKEN_PREFIX
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.user.dto.JwtTokenDTO
import gg.match.domain.user.entity.RefreshToken
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
@Transactional(readOnly = true)
class RefreshServiceDB(
    private val refreshTokenRepository: RefreshTokenRepository
) : RefreshService {

    @Value("\${jwt.refresh-token-expiry}")
    private val refreshTokenExpiry: Long = 0

    @Transactional
    override fun storeRefresh(jwtToken: JwtTokenDTO, oauth2Id: String) {
        refreshTokenRepository.deleteByOauth2Id(oauth2Id)
        refreshTokenRepository.save(
            RefreshToken(
                refreshToken = "$REFRESH_TOKEN_PREFIX:${jwtToken.refreshToken}",
                oauth2Id = oauth2Id
            )
        )
    }

    @Transactional
    override fun getRefresh(refreshToken: String): String? {
        val findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
            ?: return null

        if (!isValidate(findRefreshToken)) {
            refreshTokenRepository.delete(findRefreshToken)
            return null
        }
        return findRefreshToken.oauth2Id
    }

    override fun deleteRefresh(refreshToken: String) {
        val findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
            ?: throw BusinessException(ErrorCode.INVALID_REFRESH_TOKEN)

        refreshTokenRepository.delete(findRefreshToken)
    }

    private fun isValidate(refreshToken: RefreshToken): Boolean {
        val expiryDateTime = refreshToken.created.plus(refreshTokenExpiry, ChronoUnit.MILLIS)
        if (LocalDateTime.now().isAfter(expiryDateTime)) {
            return false
        }
        return true
    }
}