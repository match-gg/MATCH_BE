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

    var gameMode: String
)