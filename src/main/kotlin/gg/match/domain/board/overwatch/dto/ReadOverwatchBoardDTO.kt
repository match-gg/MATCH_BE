package gg.match.domain.board.overwatch.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.overwatch.entity.Position
import gg.match.domain.board.overwatch.entity.Tier
import gg.match.domain.board.overwatch.entity.Type
import java.time.LocalDateTime

class ReadOverwatchBoardDTO(
    @JsonProperty("id")
    var id: Long,

    @JsonProperty("oauth2Id")
    var oauth2Id: String,

    @JsonProperty("type")
    var type: Type,

    @JsonProperty("tier")
    var tier: Tier,

    @JsonProperty("position")
    var position: Position,

    @JsonProperty("voice")
    var voice: String,

    @JsonProperty("content")
    var content: String,

    @JsonProperty("expire")
    var expire: Expire,

    @JsonProperty("expired")
    var expired: String,

    @JsonProperty("finished")
    var finished: String,

    @JsonProperty("created")
    var created: LocalDateTime,

    @JsonProperty("author")
    var author: HeroResponseDTO,

    @JsonProperty("chatRoomId")
    var chatRoomId: String,

    @JsonProperty("memberList")
    var memberList: List<String>,

    @JsonProperty("banList")
    var banList: List<String>
)