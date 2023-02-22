package gg.match.domain.user.dto

data class JwtTokenDTO (
    val accessToken: String,
    val refreshToken: String
)