package gg.match.common.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AdminLoginDTO (
    @JsonProperty("id")
    var id: String,

    @JsonProperty("pw")
    var pw: String
)