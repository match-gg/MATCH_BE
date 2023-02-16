package gg.match.controller.api

import gg.match.domain.user.dto.SignUpRequestDTO
import gg.match.domain.user.dto.SignUpResponseDTO
import gg.match.domain.user.service.AuthService
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(
    private val authService: AuthService
) {
    @GetMapping("/test")
    fun test(): ResponseEntity<Any> {
        return ResponseEntity.ok().body("success")
    }

    @GetMapping("/kakao")
    fun kakaoCallback(@RequestParam code: String){
        print(code)
    }

    @PostMapping("/signup")
    fun signup(@RequestBody signUpRequestDTO: SignUpRequestDTO): ResponseEntity<SignUpResponseDTO> {
        return try{
            authService.signUp(signUpRequestDTO)
            ResponseEntity.ok().body(null)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }
}