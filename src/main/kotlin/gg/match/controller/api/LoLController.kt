package gg.match.controller.api

import gg.match.controller.common.dto.PageResult
import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.dto.ReadLoLBoardDTO
import gg.match.domain.board.lol.entity.Position
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type
import gg.match.domain.board.lol.service.LoLService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/lol")
class LoLController(
    private val loLService: LoLService
) {
    @GetMapping("/board")
    fun getBoards(
        @PageableDefault(size=10) pageable: Pageable,
        @RequestParam(required = false, defaultValue = "ALL") position: Position,
        @RequestParam(required = false, defaultValue = "ALL") type: Type,
        @RequestParam(required = false, defaultValue = "ALL") tier: Tier
    ): PageResult<ReadLoLBoardDTO> {
        return loLService.getBoards(pageable, position, type, tier)
    }

    @GetMapping("/board/{boardId}")
    fun getBoard(@PathVariable boardId: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(loLService.getBoard(boardId))
    }


    @PostMapping("/board")
    fun saveBoard(@RequestBody loLRequestDTO: LoLRequestDTO): ResponseEntity<Any> {
        loLRequestDTO.voice = voiceUpper(loLRequestDTO.voice)
        return try{
            ResponseEntity.ok().body(loLService.save(loLRequestDTO))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @PutMapping("/board/{boardId}")
    fun updateBoard(@PathVariable boardId: Long,
                    @RequestBody loLRequestDTO: LoLRequestDTO): ResponseEntity<Any> {
        loLRequestDTO.voice = voiceUpper(loLRequestDTO.voice)
        return try{
            ResponseEntity.ok(loLService.update(boardId, loLRequestDTO))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @DeleteMapping("/board/{boardId}")
    fun delete(@PathVariable boardId: Long): ResponseEntity<Nothing> {
        loLService.delete(boardId)
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/user/exist/{nickname}")
    fun userExist(@PathVariable nickname: String): ResponseEntity<Any> {
        return ResponseEntity.ok().body(loLService.getUserIsExist(nickname))
    }

    fun voiceUpper(voice: String): String{
        return voice.uppercase()
    }
}