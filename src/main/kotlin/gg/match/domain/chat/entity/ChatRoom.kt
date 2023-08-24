package gg.match.domain.chat.entity

import gg.match.domain.chat.dto.ChatRoomDTO
import javax.persistence.*

@Entity
@Table(name = "chatRoom")
class ChatRoom(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var chatRoomId: String,

    var nickname: String? = null,

    var oauth2Id: String,

    var isBanned: String = "false"
){
    fun update(chatRoomDTO: ChatRoomDTO){
        chatRoomId = chatRoomDTO.chatRoomId
        nickname = chatRoomDTO.nickname
        isBanned = "true"
    }
}