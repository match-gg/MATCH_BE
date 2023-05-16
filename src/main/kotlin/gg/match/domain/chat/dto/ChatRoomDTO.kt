package gg.match.domain.chat.dto

import gg.match.domain.chat.entity.ChatRoom

data class ChatRoomDTO(
    var chatRoomId: String,

    var nickname: String? = null,

    var oauth2Id: String
){
    fun toEntity(): ChatRoom? {
        return nickname?.let {
            ChatRoom(
                chatRoomId = chatRoomId,
                nickname = it,
                oauth2Id = oauth2Id
            )
        }
    }
}