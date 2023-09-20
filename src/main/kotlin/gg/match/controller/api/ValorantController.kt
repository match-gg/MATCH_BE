package gg.match.controller.api

import gg.match.common.annotation.CurrentUser
import gg.match.controller.common.dto.PageResult
import gg.match.domain.board.valorant.dto.ReadValorantBoardDTO
import gg.match.domain.board.valorant.dto.ValorantCodeRequest
import gg.match.domain.board.valorant.dto.ValorantRequestDTO
import gg.match.domain.board.valorant.entity.ValorantGameModes
import gg.match.domain.board.valorant.entity.ValorantPosition
import gg.match.domain.board.valorant.service.ValorantBoardService
import gg.match.domain.board.valorant.service.ValorantService
import gg.match.domain.user.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/valorant")
class ValorantController(
    private val valorantService: ValorantService,
    private val valorantBoardService: ValorantBoardService
) {
    //api about board
    @GetMapping("/boards")
    fun getBoards(
        @PageableDefault(size = 10) pageable: Pageable,
        @RequestParam(required = false, defaultValue = "ALL") gameMode: ValorantGameModes,
        @RequestParam(required = false, defaultValue = "ALL") position: ValorantPosition,
        @RequestParam(required = false, defaultValue = "0") tier: Long
    ): PageResult<ReadValorantBoardDTO> {
        return valorantBoardService.getBoards(pageable, gameMode, position, tier)
    }

    @GetMapping("/boards/{boardId}")
    fun getBoard(@PathVariable boardId: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(valorantBoardService.getBoard(boardId))
    }

    @PostMapping("/board")
    fun saveBoard(@CurrentUser user: User, @RequestBody valorantRequestDTO: ValorantRequestDTO): ResponseEntity<Any> {
        valorantRequestDTO.voice = voiceUpper(valorantRequestDTO.voice)
        return try{
            ResponseEntity.ok().body(valorantBoardService.save(valorantRequestDTO, user))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @PutMapping("/board/{boardId}")
    fun updateBoard(@PathVariable boardId: Long,
                    @RequestBody valorantRequestDTO: ValorantRequestDTO
    ): ResponseEntity<Any> {
        valorantRequestDTO.voice = voiceUpper(valorantRequestDTO.voice)
        return try{
            ResponseEntity.ok(valorantBoardService.update(boardId, valorantRequestDTO))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    @DeleteMapping("/board/{boardId}")
    fun delete(@PathVariable boardId: Long): ResponseEntity<Nothing> {
        valorantBoardService.delete(boardId)
        return ResponseEntity.ok().body(null)
    }

    fun voiceUpper(voice: String): String{
        return voice.uppercase()
    }

    //add user info in DB
    @GetMapping("/user/exist/{valorant}")
    fun existByUserNickname(@PathVariable valorant: String): ResponseEntity<Boolean>{
        return ResponseEntity.ok().body(valorantService.existByUserNickname(valorant.replace("%23", "#")))
    }

    @PostMapping("/user/sign")
    fun getValorantUserExist(@RequestBody valorantCodeRequest: ValorantCodeRequest): ResponseEntity<Any>{
        return ResponseEntity.ok().body(valorantService.getValorantUser(valorantCodeRequest.code))
    }

    @GetMapping("/user/{valorant}")
    fun saveValorantUser(@PathVariable valorant: String): ResponseEntity<Any>{
        return ResponseEntity.ok().body(valorantService.saveValorantUserData(valorant.replace("%23", "#")))
    }

    @GetMapping("/agent/{valorant}/{gameMode}")
    fun getAgentInfo(@PathVariable valorant: String, @PathVariable gameMode: String): ResponseEntity<Any>{
        val username = valorant.replace("%23", "#")
        val gameModes = ValorantGameModes.valueOf(gameMode.uppercase())
        return ResponseEntity.ok().body(valorantService.getAgentInfo(username, gameModes))
    }
}