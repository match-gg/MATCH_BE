package gg.match.domain.board.lol.entity

import gg.match.common.entity.BaseEntity
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.dto.ReadLoLBoardDTO
import gg.match.domain.board.lol.dto.ReadLoLListBoardDTO
import javax.persistence.*

@Entity
@Table(name = "lol")
class LoL(
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
    var position: Position,

    @Column(length=1)
    var voice: String,

    var content: String,

    @Enumerated(EnumType.STRING)
    var expire: Expire,

    var chatRoomId: String,

    var totalUser: Int = 0,

    var nowUser: Int = 1

): BaseEntity(){
    fun toReadLoLBoardDTO(summoner: Summoner, memberList: List<String>): ReadLoLBoardDTO{
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
            created = created,
            author = summoner.toSummonerResponseDTO(),
            chatRoomId = chatRoomId,
            memberList = memberList
        )
    }

    fun toReadLoLListBoardDTO(summoner: Summoner): ReadLoLListBoardDTO{
        return ReadLoLListBoardDTO(
            id = id,
            oauth2Id = oauth2Id,
            name = name,
            type = type,
            tier = tier,
            position = position,
            voice = voice,
            content = content,
            expire = expire,
            created = created,
            author = summoner.toSummonerResponseDTO(),
            chatRoomId = chatRoomId
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

    fun update(chatRoomId: String, totalUser: Int) {
        this.chatRoomId = chatRoomId
        this.totalUser = totalUser
        this.nowUser = 1
    }
}