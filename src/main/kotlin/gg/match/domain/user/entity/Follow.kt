package gg.match.domain.user.entity

import javax.persistence.*

@Entity
@Table(name = "follow")
class Follow(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var oauth2Id: String,

    var following: String
)