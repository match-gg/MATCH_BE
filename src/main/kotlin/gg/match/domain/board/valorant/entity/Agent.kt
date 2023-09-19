package gg.match.domain.board.valorant.entity

import javax.persistence.*

@Entity
@Table(name = "agent")
class Agent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var agentName: String,

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

    var looses: Long = 0,

    var heads: Long = 0,

    var shots: Long = 0,

    @Enumerated(EnumType.STRING)
    var most1Agent: ValorantCharacters = ValorantCharacters.NO_DATA,

    @Enumerated(EnumType.STRING)
    var most2Agent: ValorantCharacters = ValorantCharacters.NO_DATA,

    @Enumerated(EnumType.STRING)
    var most3Agent: ValorantCharacters = ValorantCharacters.NO_DATA
)