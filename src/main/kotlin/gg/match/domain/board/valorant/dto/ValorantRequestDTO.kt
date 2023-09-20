package gg.match.domain.board.valorant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.valorant.entity.Valorant
import gg.match.domain.board.valorant.entity.ValorantGameModes
import gg.match.domain.board.valorant.entity.ValorantPosition

data class ValorantRequestDTO (

    @JsonProperty("name")
    val name: String,

    @JsonProperty("gameMode")
    val valorantGameModes: ValorantGameModes,

    @JsonProperty("tier")
    val tier: Long,

    @JsonProperty("position")
    val position: ValorantPosition,

    @JsonProperty("voice")
    var voice: String,

    @JsonProperty("content")
    val content: String,

    @JsonProperty("expire")
    val expire: Expire
){
    fun toEntity(oauth2Id: String): Valorant {
        return Valorant(
            oauth2Id = oauth2Id,
            tier = tier,
            position = position,
            valorantGameModes = valorantGameModes,
            voice = voice,
            content = content,
            expire = expire
        )
    }
}