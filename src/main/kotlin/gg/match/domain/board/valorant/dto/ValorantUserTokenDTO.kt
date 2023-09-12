package gg.match.domain.board.valorant.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.valorant.entity.Agent


@JsonIgnoreProperties(ignoreUnknown=true)
data class ValorantUserTokenDTO (
    @JsonProperty("id_token")
    var id_token: String,

    @JsonProperty("refresh_token")
    var refresh_token: String,
){
    fun toEntity(): Agent{
        return Agent(
            id_token = id_token,
            refreshToken = refresh_token
        )
    }
}