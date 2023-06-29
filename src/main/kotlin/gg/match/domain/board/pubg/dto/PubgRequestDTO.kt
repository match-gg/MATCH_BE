package gg.match.domain.board.pubg.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.pubg.entity.Pubg
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.pubg.entity.Platform
import gg.match.domain.board.pubg.entity.Tier
import gg.match.domain.board.pubg.entity.Type

data class PubgRequestDTO(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("type")
    val type: Type,

    @JsonProperty("tier")
    val tier: Tier,

    @JsonProperty("platform")
    val platform: Platform,

    @JsonProperty("voice")
    var voice: String,

    @JsonProperty("content")
    val content: String,

    @JsonProperty("expire")
    val expire: Expire
) {
    fun toEntity(oauth2Id: String): Pubg {
        return Pubg(
            oauth2Id = oauth2Id,
            name = name,
            type = type,
            tier = tier,
            platform = platform,
            voice = voice,
            content = content,
            expire = expire,
            chatRoomId = "1234"
        )
    }
}