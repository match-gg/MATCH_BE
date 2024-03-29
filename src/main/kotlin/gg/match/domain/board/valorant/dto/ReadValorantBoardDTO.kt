package gg.match.domain.board.valorant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.common.dto.ChatMemberListDTO
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.valorant.entity.ValorantGameModes
import gg.match.domain.board.valorant.entity.ValorantPosition
import java.time.LocalDateTime

data class ReadValorantBoardDTO (
    @JsonProperty("id")
    var id: Long,

    @JsonProperty("oauth2Id")
    var oauth2Id: String,

    @JsonProperty("name")
    var name: String,

    @JsonProperty("gameMode")
    var gameMode: ValorantGameModes,

    @JsonProperty("tier")
    var tier: Long,

    @JsonProperty("position")
    var position: ValorantPosition,

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
    var author: AgentResponseDTO,

    @JsonProperty("chatRoomId")
    var chatRoomId: String,

    @JsonProperty("memberList")
    var memberList: List<ChatMemberListDTO>,

    @JsonProperty("banList")
    var banList: List<ChatMemberListDTO>
)