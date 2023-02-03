package gg.match.controller.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class UserController(
) {
    @GetMapping("/test")
    fun test(): ResponseEntity<Any> {
        return ResponseEntity.ok().body("success")
    }
}