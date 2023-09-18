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
)