package gg.match.domain.user.entity

import gg.match.common.entity.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "refresh_token")
class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var refreshTokenId: Long? = null,

    var refreshToken: String,

    var oauth2Id: String
) : BaseEntity()