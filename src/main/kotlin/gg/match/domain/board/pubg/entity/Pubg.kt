package gg.match.domain.board.pubg.entity

import gg.match.common.entity.BaseEntity
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type
import gg.match.domain.board.pubg.dto.Platform
import javax.persistence.*

@Entity
@Table(name = "pubg")
class Pubg(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var oauth2Id: String,

    var name: String,

    @Enumerated(EnumType.STRING)
    var type: Type,

    @Enumerated(EnumType.STRING)
    var tier: Tier,

    @Enumerated(EnumType.STRING)
    var platform: Platform,

    @Column(length=1)
    var voice: String,

    var content: String,

    @Enumerated(EnumType.STRING)
    var expire: Expire,

    var expired: String = "false",

    var chatRoomId: String,

    var totalUser: Int = 0,

    var nowUser: Int = 1

): BaseEntity(){
}