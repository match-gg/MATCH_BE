package gg.match.domain.user.dto

data class Oauth2UserDTO(
    val oauth2Id: String,
    val nickname: String,
    val email: String
)