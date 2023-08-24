package gg.match.domain.user.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class FollowerReturnDTO (
    @JsonProperty("oauth2Id")
    var oauth2Id: String,

    @JsonProperty("lol")
    var lol: String,

    @JsonProperty("pubg")
    var pubg: String,

    @JsonProperty("overwatch")
    var overwatch: String,

    @JsonProperty("valorant")
    var valorant: String,

)