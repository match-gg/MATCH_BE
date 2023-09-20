package gg.match.domain.board.valorant.entity

import gg.match.common.dto.ChatMemberListDTO
import gg.match.common.entity.BoardBaseEntity
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.valorant.dto.AgentResponseDTO
import gg.match.domain.board.valorant.dto.ReadValorantBoardDTO
import gg.match.domain.board.valorant.dto.ValorantRequestDTO
import javax.persistence.*

@Entity
@Table(name = "valorant")
class Valorant (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var oauth2Id: String,

    var tier: Long,

    @Enumerated(EnumType.STRING)
    var position: ValorantPosition,

    @Enumerated(EnumType.STRING)
    var valorantGameModes: ValorantGameModes,

    @Column(length=1)
    var voice: String,

    var content: String,

    @Enumerated(EnumType.STRING)
    var expire: Expire

): BoardBaseEntity(){
    fun toReadValorantBoardDTO(agent: AgentResponseDTO, memeberList: List<ChatMemberListDTO>, banList: List<ChatMemberListDTO>): ReadValorantBoardDTO{
        return ReadValorantBoardDTO(
            id = id,
            name = name,
            oauth2Id = oauth2Id,
            gameMode = valorantGameModes,
            tier = tier,
            position = position,
            voice = voice,
            content = content,
            expire = expire,
            expired = expired,
            finished = finished,
            created = created,
            author = agent,
            chatRoomId = chatRoomId,
            memberList = memeberList,
            banList = banList
        )
    }

    fun update(valorantRequestDTO: ValorantRequestDTO){
        valorantGameModes = valorantRequestDTO.valorantGameModes
        voice = valorantRequestDTO.voice
        content = valorantRequestDTO.content
        expire = valorantRequestDTO.expire
    }
}