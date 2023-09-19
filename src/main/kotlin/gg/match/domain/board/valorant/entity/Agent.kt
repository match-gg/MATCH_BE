package gg.match.domain.board.valorant.entity

import gg.match.domain.board.valorant.dto.AgentResponseDTO
import javax.persistence.*

@Entity
@Table(name = "agent")
class Agent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var name: String,

    var id_token: String,

    var puuid: String,

    var refreshToken: String,

    @Enumerated(EnumType.STRING)
    var gameMode: ValorantGameModes = ValorantGameModes.NONE,

    var tier: Long = 0,

    var avgDmg: Long = 0,

    var kills: Long = 0,

    var deaths: Long = 0,

    var wins: Long = 0,

    var losses: Long = 0,

    var heads: Long = 0,

    var shots: Long = 0,

    var most1Agent: String = "poro",

    var most2Agent: String = "poro",

    var most3Agent: String = "poro"
){
    fun toAgentResponseDTO(): AgentResponseDTO {
        return AgentResponseDTO(
            puuid = puuid,
            name = name,
            tier = tier,
            avgDmg = avgDmg,
            wins = wins,
            losses = losses,
            kills = kills,
            deaths = deaths,
            heads = heads,
            shots = shots,
            mostAgent = listOf(most1Agent, most2Agent, most3Agent)
        )
    }
}