package gg.match.domain.board.overwatch.entity

import gg.match.common.entity.BoardBaseEntity
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.overwatch.dto.HeroResponseDTO
import gg.match.domain.board.overwatch.dto.OverwatchRequestDTO
import gg.match.domain.board.overwatch.dto.ReadOverwatchBoardDTO
import javax.persistence.*

@Entity
@Table(name = "overwatch")
class Overwatch(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var oauth2Id: String,

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
    var expire: Expire,

    var expired: String = "false"

): BoardBaseEntity(){
    fun toReadOverwatchBoardDTO(hero: HeroResponseDTO, memberList: List<String>, banList: List<String>): ReadOverwatchBoardDTO {
        return ReadOverwatchBoardDTO(
            id = id,
            oauth2Id = oauth2Id,
            type = type,
            tier = tier,
            position = position,
            voice = voice,
            content = content,
            expire = expire,
            expired = expired,
            created = created,
            author = hero,
            chatRoomId = chatRoomId,
            memberList = memberList,
            banList = banList
        )
    }

    fun update(overwatchRequestDTO: OverwatchRequestDTO){
        type = overwatchRequestDTO.type
        tier = overwatchRequestDTO.tier
        position = overwatchRequestDTO.position
        voice = overwatchRequestDTO.voice
        content = overwatchRequestDTO.content
        expire = overwatchRequestDTO.expire
    }

    fun update(expired: String){
        this.expired = expired
    }
}