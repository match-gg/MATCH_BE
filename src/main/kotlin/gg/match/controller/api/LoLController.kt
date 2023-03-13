package gg.match.controller.api

import gg.match.controller.common.dto.PageResult
import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.dto.ReadLoLBoardDTO
import gg.match.domain.board.lol.service.LoLService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/lol/board")
class LoLController(
    private val loLService: LoLService
) {
    @GetMapping
    fun getBoards(@PageableDefault(size=10) pageable: Pageable): PageResult<ReadLoLBoardDTO> {
        return loLService.getBoards(pageable)
    }

    @GetMapping("/{boardId}")
    fun getBoard(@PathVariable boardId: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(loLService.getBoard(boardId))
    }


    @PostMapping
    fun saveBoard(@RequestBody loLRequestDTO: LoLRequestDTO): ResponseEntity<Any> {
        loLRequestDTO.voice = voiceUpper(loLRequestDTO.voice)
        return try{
            ResponseEntity.ok().body(loLService.save(loLRequestDTO))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @PutMapping("/{boardId}")
    fun updateBoard(@PathVariable boardId: Long,
                    @RequestBody loLRequestDTO: LoLRequestDTO): ResponseEntity<Any> {
        loLRequestDTO.voice = voiceUpper(loLRequestDTO.voice)
        return try{
            ResponseEntity.ok(loLService.update(boardId, loLRequestDTO))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @DeleteMapping("/{boardId}")
    fun delete(@PathVariable boardId: Long): ResponseEntity<Nothing> {
        loLService.delete(boardId)
        return ResponseEntity.ok().body(null)
    }

    fun voiceUpper(voice: String): String{
        return voice.uppercase()
    }
}