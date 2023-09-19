package gg.match.controller.api

import gg.match.domain.board.valorant.dto.ValorantCodeRequest
import gg.match.domain.board.valorant.service.ValorantService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/valorant")
class ValorantController(
    private val valorantService: ValorantService
) {
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
}