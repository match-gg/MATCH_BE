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
    @PostMapping("/user/exist")
    fun test(@RequestBody valorantCodeRequest: ValorantCodeRequest): ResponseEntity<Any>{
        return ResponseEntity.ok().body(valorantService.getValorantUser(valorantCodeRequest.code))
    }
}