package gg.match.domain.board.lol.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.lol.entity.Position
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type
import java.time.LocalDateTime

class ReadLoLBoardDTO(
    @JsonProperty("id")
    var id: Long,

    @JsonProperty("name")
    var name: String,

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

    @JsonProperty("created")
    var created: LocalDateTime,

    @JsonProperty("author")
    var author: SummonerResponseDTO
)