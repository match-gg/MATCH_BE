package gg.match.domain.board.lol.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.lol.entity.LoL
import gg.match.domain.board.lol.entity.Position
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type

data class LoLRequestDTO(

    @JsonProperty("type")
    val type: Type,

    @JsonProperty("tier")
    val tier: Tier,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("position")
    val position: Position,

    @JsonProperty("voice")
    var voice: String,

    @JsonProperty("content")
    val content: String,

    @JsonProperty("expire")
    val expire: Expire
) {
    fun toEntity(oauth2Id: String): LoL {
        return LoL(
            oauth2Id = oauth2Id,
            type = type,
            tier = tier,
            position = position,
            voice = voice,
            content = content,
            expire = expire
        )
    }
}