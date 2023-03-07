package gg.match.domain.user.entity

import gg.match.domain.user.dto.Oauth2UserDTO
import gg.match.domain.user.dto.RegisterDTO
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var oauth2Id: String,

    var nickname: String,

    var email: String?,

    @Enumerated(EnumType.STRING)
    var representative: Game,

    var lol: String?,

    var overwatch: String?,

    var pubg: String?,

    var maplestory: String?,

    var lostark: String?,

    @CreatedDate
    @Column(name = "created", updatable = false)
    var regdate: LocalDate

) {
    companion object {
        fun of(oAuth2User: Oauth2UserDTO): User {
            return User(
                oauth2Id = oAuth2User.oauth2Id,
                nickname = oAuth2User.nickname,
                email = oAuth2User.email,
                representative = Game.NONE,
                lol = null,
                overwatch = null,
                pubg = null,
                maplestory = null,
                lostark = null,
                regdate = LocalDate.now()
            )
        }
    }

    fun register(registerDTO: RegisterDTO) : User{
        return User(
            oauth2Id = oauth2Id,
            nickname = nickname,
            email = email,
            representative = registerDTO.representative,
            lol = registerDTO.lol,
            overwatch = registerDTO.overwatch,
            pubg = registerDTO.pubg,
            maplestory = registerDTO.maplestory,
            lostark = registerDTO.lostark,
            regdate = regdate
        )
    }
}