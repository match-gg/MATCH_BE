package gg.match.domain.chat.entity

import javax.persistence.*

@Entity
@Table(name = "chatRoom")
class ChatRoom(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var chatRoomId: String,

    var nickname: String? = null,

    var oauth2Id: String
)