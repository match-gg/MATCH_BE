package gg.match.domain.user.entity

import gg.match.domain.user.dto.Oauth2UserDTO
import gg.match.domain.user.dto.SignUpRequestDTO
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var oauth2Id: String,

    var nickname: String,

    var email: String?,

    var imageUrl: String?,

    @Enumerated(EnumType.STRING)
    var representative: Game,

    var lol: String?,

    var overwatch: String?,

    var pubg: String?,

    var maplestory: String?,

    var lostark: String?,

    var likeCount: Long = 0,

    var dislikeCount: Long = 0,

    var matchCount: Long = 0,

    @CreatedDate
    @Column(name = "created", updatable = false)
    var created: LocalDateTime = LocalDateTime.now()

) {
    companion object {
        fun of(oAuth2User: Oauth2UserDTO, signUpRequestDTO: SignUpRequestDTO): User {
            return User(
                oauth2Id = oAuth2User.oauth2Id,
                nickname = oAuth2User.nickname,
                email = oAuth2User.email,
                imageUrl = oAuth2User.imageUrl,
                representative = signUpRequestDTO.representative,
                lol = signUpRequestDTO.lol,
                overwatch = signUpRequestDTO.overwatch,
                pubg = signUpRequestDTO.pubg,
                maplestory = signUpRequestDTO.maplestory,
                lostark = signUpRequestDTO.lostark
            )
        }
    }
}