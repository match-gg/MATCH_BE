package gg.match.common.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TotalUserRequestDTO (
    @JsonProperty("totalUser")
    var totalUser: Int
)