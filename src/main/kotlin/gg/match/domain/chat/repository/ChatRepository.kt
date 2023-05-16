package gg.match.domain.chat.repository

import gg.match.domain.chat.dto.ChatRoomDTO
import gg.match.domain.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository: JpaRepository<ChatRoom, Long> {
    fun findAllByOauth2Id(oauth2Id: String): List<ChatRoom>
    fun findByChatRoomId(chatRoomId: String): ChatRoomDTO
    fun findByChatRoomIdAndOauth2Id(chatRoomId: String, oauth2Id: String): ChatRoomDTO
    fun findAllByChatRoomId(chatRoomId: String): List<ChatRoom>
}