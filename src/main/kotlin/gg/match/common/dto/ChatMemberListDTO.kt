package gg.match.common.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatMemberListDTO (

    @JsonProperty("oauth2Id")
    var oauth2Id: String,

    @JsonProperty("nickname")
    var nickname: String
)