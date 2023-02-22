package gg.match.domain.user.service.refresh

import gg.match.domain.user.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByRefreshToken(refreshToken: String): RefreshToken
    fun deleteByOauth2Id(oauth2Id: String)
}