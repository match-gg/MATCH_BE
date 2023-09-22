package gg.match.domain.user.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LikeRequestDTO (
    @JsonProperty("oauth2Id")
    val oauth2Id: String
)