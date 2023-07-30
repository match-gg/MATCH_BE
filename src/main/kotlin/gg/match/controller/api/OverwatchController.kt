package gg.match.controller.api

import gg.match.common.annotation.CurrentUser
import gg.match.controller.common.dto.PageResult
import gg.match.domain.board.overwatch.dto.HeroResponseDTO
import gg.match.domain.board.overwatch.dto.OverwatchRequestDTO
import gg.match.domain.board.overwatch.dto.ReadOverwatchBoardDTO
import gg.match.domain.board.overwatch.entity.*
import gg.match.domain.board.overwatch.service.OverwatchService
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
        @RequestParam(required = false, defaultValue = "ALL") position: Position,
        @RequestParam(required = false, defaultValue = "ALL") type: Type,
        @RequestParam(required = false, defaultValue = "ALL") tier: Tier
    ): PageResult<ReadOverwatchBoardDTO> {
        return overwatchService.getBoards(pageable, position, type, tier)
    }

    @GetMapping("/boards/{boardId}")
    fun getBoard(@PathVariable boardId: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(overwatchService.getBoard(boardId))
    }

    @PostMapping("/board")
    fun saveBoard(@CurrentUser user: User, @RequestBody overwatchRequestDTO: OverwatchRequestDTO): ResponseEntity<Any> {
        overwatchRequestDTO.voice = voiceUpper(overwatchRequestDTO.voice)
        return try{
            ResponseEntity.ok().body(overwatchService.save(overwatchRequestDTO, user))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @PutMapping("/board/{boardId}")
    fun updateBoard(@PathVariable boardId: Long,
                    @RequestBody overwatchRequestDTO: OverwatchRequestDTO
    ): ResponseEntity<Any> {
        overwatchRequestDTO.voice = voiceUpper(overwatchRequestDTO.voice)
        return try{
            ResponseEntity.ok(overwatchService.update(boardId, overwatchRequestDTO))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @DeleteMapping("/board/{boardId}")
    fun delete(@PathVariable boardId: Long): ResponseEntity<Nothing> {
        overwatchService.delete(boardId)
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/user/exist/{nickname}")
    fun userExist(@PathVariable nickname: String): ResponseEntity<Any> {
        return ResponseEntity.ok().body(overwatchService.getHeroIsExist(replaceString(nickname)))
    }

    @GetMapping("/user/{nickname}")
    fun saveHero(@PathVariable nickname: String): ResponseEntity<Any> {
        overwatchService.saveHeroInfoByBattleNetApi(replaceString(nickname))
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/player/{nickname}/{type}")
    fun getHeroInfo(@PathVariable nickname: String, @PathVariable type: Type): ResponseEntity<HeroResponseDTO> {
        return ResponseEntity.ok().body(overwatchService.getHeroInfo(replaceString(nickname), type))
    }

    fun replaceString(name: String): String{
        return name.replace("%23", "#")
    }

    fun voiceUpper(voice: String): String{
        return voice.uppercase()
    }
}