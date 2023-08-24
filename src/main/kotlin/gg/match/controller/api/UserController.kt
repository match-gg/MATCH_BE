package gg.match.controller.api

import gg.match.common.annotation.CurrentUser
import gg.match.common.jwt.util.JwtResolver
import gg.match.controller.common.dto.PageResult
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.lol.service.LoLService
import gg.match.domain.board.overwatch.service.OverwatchService
import gg.match.domain.board.pubg.service.PubgService
import gg.match.domain.user.dto.*
import gg.match.domain.user.entity.Game
import gg.match.domain.user.entity.User
import gg.match.domain.user.repository.FollowRepository
import gg.match.domain.user.service.AuthService
import gg.match.domain.user.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/user")
class UserController(
    private val authService: AuthService,
    private val userService: UserService,
    private val jwtResolver: JwtResolver,

    private val loLService: LoLService,
    private val pubgService: PubgService,
    private val overwatchService: OverwatchService,

    private val followRepository: FollowRepository
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

    @PutMapping("/{game}/{nickname}/nickname")
    fun changeNickname(@PathVariable game: Game, @PathVariable nickname: String, @CurrentUser user: User): ResponseEntity<Any> {
        return ResponseEntity.ok().body(userService.changeNickname(game, nickname, user))
    }

    @PutMapping("/{game}/representative")
    fun changeRepresentative(@PathVariable game: Game, @CurrentUser user: User): ResponseEntity<Any> {
        return ResponseEntity.ok().body(userService.changeRepresentative(game, user))
    }

    @PostMapping("/like")
    fun increaseLike(@RequestBody likeRequestDTO: LikeRequestDTO): ResponseEntity<Long> {
        return ResponseEntity.ok().body(userService.increaseLike(likeRequestDTO))
    }

    @PostMapping("/dislike")
    fun increaseDislike(@RequestBody likeRequestDTO: LikeRequestDTO): ResponseEntity<Long> {
        return ResponseEntity.ok().body(userService.increaseDislike(likeRequestDTO))
    }

    @GetMapping("/play/info")
    fun getPlayInfoByUser(@RequestParam(required = true) oauth2Id: String): ResponseEntity<UserPlayInfoDTO>{
        return ResponseEntity.ok().body(userService.getUserPlayInfo(oauth2Id))
    }

    @PostMapping("/follow")
    fun following(@CurrentUser user: User, @RequestParam(required = true) oauth2Id: String): ResponseEntity<Any>{
        return ResponseEntity.ok(userService.following(user, oauth2Id))
    }

    @DeleteMapping("/follow")
    fun cancelFollowing(@CurrentUser user: User, @RequestParam(required = true) oauth2Id: String): ResponseEntity<Any>{
        return ResponseEntity.ok(userService.cancelFollowing(user, oauth2Id))
    }

    @GetMapping("/follow/list")
    fun getFollowList(@CurrentUser user: User): ResponseEntity<FollowerReturnWrapDTO>{
        return ResponseEntity.ok().body(userService.getFollower(user))
    }

    @GetMapping("/follower/boards")
    fun getFollowerBoards(
        @CurrentUser user: User,
        @PageableDefault(size = 3) pageable: Pageable,
        @RequestParam(required = true) game: String
    ): ResponseEntity<PageResult<out Any>> {
        val followers = followRepository.findAllByOauth2Id(user.oauth2Id)
        val oauth2Ids = mutableListOf<String>()
        for(element in followers){
            oauth2Ids.add(element.following)
        }

        val result = when(Game.valueOf(game)){
            Game.LOL -> loLService.getFollowerBoards(user, pageable, oauth2Ids)
            Game.PUBG -> pubgService.getFollowerBoards(user, pageable, oauth2Ids)
            Game.OVERWATCH -> overwatchService.getFollowerBoards(user, pageable, oauth2Ids)
            else -> throw BusinessException(ErrorCode.BAD_REQUEST)
        }
        return ResponseEntity.ok().body(result)
    }
}