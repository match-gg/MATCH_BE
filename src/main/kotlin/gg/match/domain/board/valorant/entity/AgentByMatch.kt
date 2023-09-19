package gg.match.domain.board.valorant.entity

import javax.persistence.*

@Entity
@Table(name = "agent_by_match")
class AgentByMatch(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var puuid: String,

    var matchId: String,

    var agentName: String,

    var characterName: String,

    var gameMode: String,

    var avgDmg: Long,

    var head: Long,

    var shots: Long,

    var kills: Long,

    var deaths: Long,

    var won: String,

    var tier: Long,

    var isRanked: String
)