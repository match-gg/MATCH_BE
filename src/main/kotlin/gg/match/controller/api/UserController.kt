package gg.match.controller.api

import gg.match.common.annotation.CurrentUser
import gg.match.common.jwt.util.JwtResolver
import gg.match.domain.user.dto.*
import gg.match.domain.user.entity.User
import gg.match.domain.user.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import org.springframework.security.core.userdetails.UserDetails

@RestController
@RequestMapping("/api/user")
class UserController(
    private val authService: AuthService,
    private val jwtResolver: JwtResolver
) {
    @PostMapping("/signup")
    fun signup(@RequestBody signUpRequestDTO: SignUpRequestDTO): ResponseEntity<JwtTokenDTO> {
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
    fun myInfo(request: HttpServletRequest): ResponseEntity<Any> {
        val user = jwtResolver.getFromSecurityContextHolder()
        return ResponseEntity.ok().body(user.username)
//        return ResponseEntity.ok().body(user.oauth2Id)
    }
}