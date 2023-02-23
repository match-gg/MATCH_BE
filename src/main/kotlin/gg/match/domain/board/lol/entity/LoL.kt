package gg.match.domain.board.lol.entity

import gg.match.controller.common.entity.Expire
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "lol")
class LoL(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    var type: Type,
    var tier: Tier,
    var position: Position,
    var voice: Boolean,
    var content: String,
    var expire: Expire,
    var regdate: Date
)