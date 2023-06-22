package gg.match.controller.api

import gg.match.common.annotation.CurrentUser
import gg.match.controller.common.dto.PageResult
import gg.match.domain.board.lol.dto.*
import gg.match.domain.board.lol.entity.LoL
import gg.match.domain.board.lol.entity.Position
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type
import gg.match.domain.board.lol.service.LoLService
import gg.match.domain.user.entity.User
import org.json.simple.JSONObject
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
    @GetMapping("/boards")
    fun getBoards(
        @PageableDefault(size = 10) pageable: Pageable,
        @RequestParam(required = false, defaultValue = "ALL") position: Position,
        @RequestParam(required = false, defaultValue = "ALL") type: Type,
        @RequestParam(required = false, defaultValue = "ALL") tier: Tier
    ): PageResult<ReadLoLBoardDTO> {
        return loLService.getBoards(pageable, position, type, tier)
    }

    @GetMapping("/boards/{boardId}")
    fun getBoard(@PathVariable boardId: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(loLService.getBoard(boardId))
    }


    @PostMapping("/board")
    fun saveBoard(@CurrentUser user: User, @RequestBody loLRequestDTO: LoLRequestDTO): ResponseEntity<Any> {
        loLRequestDTO.voice = voiceUpper(loLRequestDTO.voice)
        return try{
            ResponseEntity.ok().body(loLService.save(loLRequestDTO, user))
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
        val userName: JSONObject = loLService.getUserInfoByRiotApi(nickname)
        return ResponseEntity.ok().body(userName["name"])
    }

    @GetMapping("/user/{nickname}")
    fun saveUserByRiot(@PathVariable nickname: String): ResponseEntity<Any> {
        loLService.saveUserInfoByRiotApi(nickname)
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/summoner/{nickname}/{type}")
    fun getSummonerInfo(@PathVariable nickname: String, @PathVariable type: String): ResponseEntity<SummonerResponseDTO> {
        return ResponseEntity.ok().body(loLService.getSummonerByType(nickname, Type.valueOf(type.uppercase())).toSummonerResponseDTO())
    }

    fun voiceUpper(voice: String): String{
        return voice.uppercase()
    }
}