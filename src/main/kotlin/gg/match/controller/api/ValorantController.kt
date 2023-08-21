package gg.match.controller.api

import gg.match.domain.board.valorant.service.ValorantService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/valorant")
class ValorantController(
    private val valorantService: ValorantService
) {
    @GetMapping("/test")
    fun test(): ResponseEntity<Any>{
        return ResponseEntity.ok().body(null)
    }
}