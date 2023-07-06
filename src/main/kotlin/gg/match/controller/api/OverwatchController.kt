package gg.match.controller.api

import gg.match.common.annotation.CurrentUser
import gg.match.controller.common.dto.PageResult
import gg.match.domain.board.pubg.dto.PlayerResponseDTO
import gg.match.domain.board.pubg.dto.PubgRequestDTO
import gg.match.domain.board.pubg.entity.Tier
import gg.match.domain.board.pubg.entity.Type
import gg.match.domain.board.pubg.dto.ReadPubgBoardDTO
import gg.match.domain.board.pubg.entity.Platform
import gg.match.domain.board.pubg.service.PubgService
import gg.match.domain.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/overwatch")
class OverwatchController (
    private val overwatchService: OverwatchService
){
    @GetMapping("/boards")
    fun getBoards(
        @PageableDefault(size = 10) pageable: Pageable,
        @RequestParam(required = false, defaultValue = "ALL") platform: Platform,
        @RequestParam(required = false, defaultValue = "ALL") type: Type,
        @RequestParam(required = false, defaultValue = "ALL") tier: Tier
    ): PageResult<ReadPubgBoardDTO> {
        return pubgService.getBoards(pageable, platform, type, tier)
    }

    @GetMapping("/boards/{boardId}")
    fun getBoard(@PathVariable boardId: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(pubgService.getBoard(boardId))
    }

    @PostMapping("/board")
    fun saveBoard(@CurrentUser user: User, @RequestBody pubgRequestDTO: PubgRequestDTO): ResponseEntity<Any> {
        pubgRequestDTO.voice = voiceUpper(pubgRequestDTO.voice)
        return try{
            ResponseEntity.ok().body(pubgService.save(pubgRequestDTO, user))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @PutMapping("/board/{boardId}")
    fun updateBoard(@PathVariable boardId: Long,
                    @RequestBody pubgRequestDTO: PubgRequestDTO
    ): ResponseEntity<Any> {
        pubgRequestDTO.voice = voiceUpper(pubgRequestDTO.voice)
        return try{
            ResponseEntity.ok(pubgService.update(boardId, pubgRequestDTO))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @DeleteMapping("/board/{boardId}")
    fun delete(@PathVariable boardId: Long): ResponseEntity<Nothing> {
        pubgService.delete(boardId)
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/user/exist/{nickname}/{platform}")
    fun userExist(@PathVariable nickname: String, @PathVariable platform: String): ResponseEntity<Any> {
        val account = pubgService.getPlayerAccountId(nickname, Platform.valueOf(platform.uppercase()))
        return ResponseEntity.ok().body(account)
    }

    @GetMapping("/user/{nickname}/{platform}")
    fun savePlayer(@PathVariable nickname: String, @PathVariable platform: String): ResponseEntity<Any> {
        pubgService.getPlayerInfoByPubgApi(nickname, Platform.valueOf(platform.uppercase()))
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/player/{nickname}/{platform}/{type}")
    fun getPlayerInfo(@PathVariable nickname: String, @PathVariable platform: String, @PathVariable type: String): ResponseEntity<PlayerResponseDTO> {
        return ResponseEntity.ok().body(pubgService.getPlayerByPlatformAndType(nickname, Platform.valueOf(platform.uppercase()), Type.valueOf(type.uppercase())))
    }

    fun voiceUpper(voice: String): String{
        return voice.uppercase()
    }
}