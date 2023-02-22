package gg.match.domain.user.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SignInRequestDTO (
    @JsonProperty("oauth2AccessToken")
    val oauth2AccessToken: String
)