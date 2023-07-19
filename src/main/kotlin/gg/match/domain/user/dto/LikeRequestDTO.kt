package gg.match.domain.user.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LikeRequestDTO (
    @JsonProperty("game")
    val game: String,

    @JsonProperty("nickname")
    val nickname: String
)