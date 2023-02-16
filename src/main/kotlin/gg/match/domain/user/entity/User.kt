package gg.match.domain.user.entity

import gg.match.domain.user.dto.Oauth2UserDTO
import javax.persistence.*

@Entity
@Table(name = "user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var oauth2Id: String,
    var nickname: String,
    var email: String?
) {
    companion object {
        fun of(oAuth2User: Oauth2UserDTO): User {
            return User(
                oauth2Id = oAuth2User.oauth2Id,
                nickname = oAuth2User.nickname,
                email = oAuth2User.email,
            )
        }
    }
}