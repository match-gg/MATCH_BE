package gg.match.domain.chat.repository

import gg.match.domain.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRepository: JpaRepository<ChatRoom, Long> {
    fun findAllByOauth2Id(oauth2Id: String): List<ChatRoom>
    fun findAllByChatRoomId(chatRoomId: String): List<ChatRoom>
    fun findAllByChatRoomIdAndOauth2Id(chatRoomId: String, oauth2Id: String): List<ChatRoom>
    fun deleteByChatRoomIdAndNickname(chatRoomId: String, nickname: String)
    fun existsByChatRoomIdAndNickname(chatRoomId: String, nickname: String): Boolean
    fun findByChatRoomIdAndNickname(chatRoomId: String, nickname: String): ChatRoom
}