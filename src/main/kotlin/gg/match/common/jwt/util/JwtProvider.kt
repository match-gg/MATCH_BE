package gg.match.common.jwt.util

import gg.match.domain.user.service.UserService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*

@Component
@Transactional(readOnly = true)
class JwtProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-token-expiry}") private val accessTokenValidTime: Int,
    @Value("\${jwt.refresh-token-expiry}") private val refreshTokenValidTime: Long,
    private val userService: UserService
) {
    private val key: Key = Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))
    fun createAccessToken(oAuth2Id: String): String {
        val user = userService.findByOauth2Id(oAuth2Id)
        val claims: Claims = Jwts.claims().setSubject(oAuth2Id)
        claims["oAuth2Id"] = oAuth2Id
        claims["nickname"] = user?.nickname
        claims["imageUrl"] = user?.imageUrl
        claims["representative"] = user?.representative
        val now = Date()
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + accessTokenValidTime))
            .signWith(key)
            .compact()
    }

    fun createRefreshToken(): String {
        val now = Date()
        return Jwts.builder()
            .setIssuedAt(now)
            .setExpiration(Date(now.time + refreshTokenValidTime))
            .signWith(key)
            .compact()
    }
}