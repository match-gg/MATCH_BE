package gg.match.controller.api

import gg.match.domain.board.valorant.dto.ValorantCodeRequest
import gg.match.domain.board.valorant.service.ValorantService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/valorant")
class ValorantController(
    private val valorantService: ValorantService
) {
    @GetMapping("/user/exist")
    fun test(@RequestBody valorantCodeRequest: ValorantCodeRequest): ResponseEntity<Any>{
        return ResponseEntity.ok().body(valorantService.getValorantUser(valorantCodeRequest.code))
    }
}