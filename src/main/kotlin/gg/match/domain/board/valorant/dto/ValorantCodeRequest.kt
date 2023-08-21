package gg.match.domain.board.valorant.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ValorantCodeRequest(
    @JsonProperty("code")
    val code: String
){
}