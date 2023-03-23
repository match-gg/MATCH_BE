package gg.match.controller.api

import gg.match.common.annotation.CurrentUser
import gg.match.common.jwt.util.JwtResolver
import gg.match.domain.board.lol.service.LoLService
import gg.match.domain.user.dto.*
import gg.match.domain.user.entity.User
import gg.match.domain.user.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/user")
class UserController(
    private val authService: AuthService,
    private val loLService: LoLService,
    private val jwtResolver: JwtResolver
) {
    @PostMapping("/signup")
    fun signup(@RequestBody signUpRequestDTO: SignUpRequestDTO): ResponseEntity<JwtTokenDTO> {
        signUpRequestDTO.lol?.let { loLService.saveUserInfoByRiotApi(it) }
        return ResponseEntity.ok().body(authService.signUp(signUpRequestDTO))
    }

    @PostMapping("/signin")
    fun signin(@RequestBody signInRequestDTO: SignInRequestDTO): ResponseEntity<JwtTokenDTO> {
        return ResponseEntity.ok().body(authService.signin(signInRequestDTO))
    }

    @PostMapping("/refresh")
    fun refresh(request: HttpServletRequest): ResponseEntity<JwtTokenDTO> {
        val refreshToken = jwtResolver.resolveRefreshToken(request)
        return ResponseEntity.ok().body(authService.refresh(refreshToken))
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Any> {
        val accessToken = jwtResolver.resolveAccessToken(request)
        val refreshToken = jwtResolver.resolveRefreshToken(request)
        authService.logout(refreshToken, accessToken)
        return ResponseEntity.ok().body(null)
    }

    @GetMapping("/info")
    fun myInfo(request: HttpServletRequest, @CurrentUser user: User): ResponseEntity<Any> {
        return ResponseEntity.ok().body(user)
    }

    @GetMapping("/test")
    fun test(): ResponseEntity<Any>{
        return ResponseEntity.ok().body(LocalDateTime.now())
    }
}