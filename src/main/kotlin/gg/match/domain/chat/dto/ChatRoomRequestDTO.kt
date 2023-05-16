package gg.match.domain.chat.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatRoomRequestDTO(
    @JsonProperty("boardId")
    val boardId: Long,

    @JsonProperty("chatRoomId")
    val chatRoomId: String,

    @JsonProperty("totalUser")
    val totalUser: Int
)