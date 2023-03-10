package gg.match.domain.board.lol.entity

import gg.match.controller.common.entity.BaseEntity
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.dto.ReadLoLBoardDTO
import java.time.LocalDate
import javax.persistence.*
import kotlin.math.exp

@Entity
@Table(name = "lol")
class LoL(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var name: String,

    @Enumerated(EnumType.STRING)
    var type: Type,

    @Enumerated(EnumType.STRING)
    var tier: Tier,

    @Enumerated(EnumType.STRING)
    var position: Position,

    @Column(length=1)
    var voice: String,

    var content: String,

    @Enumerated(EnumType.STRING)
    var expire: Expire

): BaseEntity(){
    fun toReadLoLBoardDTO(): ReadLoLBoardDTO{
        return ReadLoLBoardDTO(
            id = id,
            name = name,
            type = type,
            tier = tier,
            position = position,
            voice = voice,
            content = content,
            expire = expire
        )
    }

    fun update(lolRequestDTO: LoLRequestDTO){
        name = lolRequestDTO.name
        type = lolRequestDTO.type
        tier = lolRequestDTO.tier
        position = lolRequestDTO.position
        voice = lolRequestDTO.voice
        content = lolRequestDTO.content
        expire = lolRequestDTO.expire
    }
}