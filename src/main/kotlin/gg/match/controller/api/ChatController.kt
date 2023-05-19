package gg.match.controller.api

import gg.match.common.annotation.CurrentUser
import gg.match.domain.chat.dto.ChatRoomListDTO
import gg.match.domain.chat.dto.ChatRoomRequestDTO
import gg.match.domain.chat.service.ChatService
import gg.match.domain.user.entity.Game
import gg.match.domain.user.entity.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService
) {
    @GetMapping("/rooms")
    fun getChatRooms(@CurrentUser user: User): ResponseEntity<ChatRoomListDTO> {
        return ResponseEntity.ok(chatService.getChatRooms(user))
    }

    @PostMapping("/{game}")
    fun saveChatRoomId(@PathVariable game: String, @RequestBody chatRoomRequestDTO: ChatRoomRequestDTO, @CurrentUser user: User): ResponseEntity<Any> {
        return ResponseEntity.ok(chatService.saveChatRoomId(Game.valueOf(game.uppercase()), chatRoomRequestDTO, user))
    }

    @PostMapping("/{game}/{id}/member")
    fun increaseMember(@PathVariable game: String, @PathVariable id: Long, @CurrentUser user: User): ResponseEntity<Any>{
        return ResponseEntity.ok(chatService.increaseMember(Game.valueOf(game.uppercase()), id, user))
    }

    @PostMapping("/{game}/{id}/{nickname}")
    fun addMember(@PathVariable game: String, @PathVariable id: Long, @PathVariable nickname: String): ResponseEntity<Any>{
        return ResponseEntity.ok(chatService.addMember(Game.valueOf(game.uppercase()), id, nickname))
    }

    @DeleteMapping("/{game}/{id}/member")
    fun decreaseMember(@PathVariable game: String, @PathVariable id: Long, @CurrentUser user: User): ResponseEntity<Any>{
        return ResponseEntity.ok(chatService.decreaseMember(Game.valueOf(game.uppercase()), id, user.oauth2Id))
    }

    @DeleteMapping("/{game}/{id}/{nickname}")
    fun kickMember(@PathVariable game: String, @PathVariable id: Long, @PathVariable nickname: String): ResponseEntity<Any>{
        return ResponseEntity.ok(chatService.decreaseMember(Game.valueOf(game.uppercase()), id, nickname))
    }
}