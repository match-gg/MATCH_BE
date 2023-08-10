package gg.match.domain.board.overwatch.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.overwatch.entity.Overwatch
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.overwatch.entity.Position
import gg.match.domain.board.overwatch.entity.Tier
import gg.match.domain.board.overwatch.entity.Type

data class OverwatchRequestDTO(

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
    fun toEntity(oauth2Id: String): Overwatch {
        return Overwatch(
            oauth2Id = oauth2Id,
            type = this.type,
            tier = this.tier,
            position = this.position,
            voice = this.voice,
            content = this.content,
            expire = this.expire
        )
    }
}