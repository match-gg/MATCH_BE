package gg.match.controller.api

import gg.match.common.dto.AdminLoginDTO
import gg.match.domain.user.dto.JwtTokenDTO
import gg.match.domain.user.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
class AdminController (
    private val authService: AuthService
){
    @PostMapping("/login")
    fun signin(@RequestBody adminLoginDTO: AdminLoginDTO): ResponseEntity<JwtTokenDTO> {
        return ResponseEntity.ok().body(authService.adminLogin(adminLoginDTO))
    }
}