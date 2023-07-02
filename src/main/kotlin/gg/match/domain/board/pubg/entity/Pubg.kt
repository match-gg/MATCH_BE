package gg.match.domain.board.pubg.entity

import gg.match.common.entity.BaseEntity
import gg.match.common.entity.BoardBaseEntity
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.pubg.dto.PlayerResponseDTO
import gg.match.domain.board.pubg.dto.PubgRequestDTO
import gg.match.domain.board.pubg.dto.ReadPubgBoardDTO
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

    var expired: String = "false"

): BoardBaseEntity(){
    fun toReadPubgBoardDTO(player: PlayerResponseDTO, memberList: List<String>, banList: List<String>): ReadPubgBoardDTO {
        return ReadPubgBoardDTO(
            id = id,
            oauth2Id = oauth2Id,
            name = name,
            type = type,
            tier = tier,
            platform = platform,
            voice = voice,
            content = content,
            expire = expire,
            expired = expired,
            created = created,
            author = player,
            chatRoomId = chatRoomId,
            memberList = memberList,
            banList = banList
        )
    }

    fun update(pubgRequestDTO: PubgRequestDTO){
        name = pubgRequestDTO.name
        type = pubgRequestDTO.type
        tier = pubgRequestDTO.tier
        platform = pubgRequestDTO.platform
        voice = pubgRequestDTO.voice
        content = pubgRequestDTO.content
        expire = pubgRequestDTO.expire
    }

    fun update(expired: String){
        this.expired = expired
    }
}