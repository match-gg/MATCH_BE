package gg.match.controller.api

import gg.match.common.annotation.CurrentUser
import gg.match.common.jwt.util.JwtResolver
import gg.match.domain.user.dto.*
import gg.match.domain.user.entity.User
import gg.match.domain.user.service.AuthService
import gg.match.domain.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/user")
class UserController(
    private val authService: AuthService,
    private val userService: UserService,
    private val jwtResolver: JwtResolver
) {
    @GetMapping("/health")
    fun healthCheck(): ResponseEntity<Any>{
        return ResponseEntity.ok().body(null)
    }

    @PostMapping("/signup")
    fun signup(@RequestBody signUpRequestDTO: SignUpRequestDTO): ResponseEntity<Any> {
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

    @PostMapping("/like")
    fun increaseLike(@RequestBody likeRequestDTO: LikeRequestDTO): ResponseEntity<Long> {
        return ResponseEntity.ok().body(userService.increaseLike(likeRequestDTO))
    }

    @PostMapping("/dislike")
    fun increaseDislike(@RequestBody likeRequestDTO: LikeRequestDTO): ResponseEntity<Long> {
        return ResponseEntity.ok().body(userService.increaseDislike(likeRequestDTO))
    }

    @PostMapping("/finished/{boardId}")
    fun finishedBoard(): ResponseEntity<Any>{
        return ResponseEntity.ok().body(userService.finishedBoard())
    }
}