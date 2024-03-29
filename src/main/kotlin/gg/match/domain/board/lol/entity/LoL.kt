package gg.match.domain.board.lol.entity

import gg.match.common.dto.ChatMemberListDTO
import gg.match.common.entity.BoardBaseEntity
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.dto.ReadLoLBoardDTO
import javax.persistence.*

@Entity
@Table(name = "lol")
class LoL(
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
    var expire: Expire

): BoardBaseEntity(){
    fun toReadLoLBoardDTO(summoner: Summoner, memberList: List<ChatMemberListDTO>, banList: List<ChatMemberListDTO>): ReadLoLBoardDTO{
        return ReadLoLBoardDTO(
            id = id,
            oauth2Id = oauth2Id,
            name = name,
            type = type,
            tier = tier,
            position = position,
            voice = voice,
            content = content,
            expire = expire,
            expired = expired,
            finished = finished,
            created = created,
            author = summoner.toSummonerResponseDTO(),
            chatRoomId = chatRoomId,
            memberList = memberList,
            banList = banList,
        )
    }

    fun update(lolRequestDTO: LoLRequestDTO){
        type = lolRequestDTO.type
        tier = lolRequestDTO.tier
        position = lolRequestDTO.position
        voice = lolRequestDTO.voice
        content = lolRequestDTO.content
        expire = lolRequestDTO.expire
    }
}