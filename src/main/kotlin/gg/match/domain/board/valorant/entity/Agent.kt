package gg.match.domain.board.valorant.entity

import javax.persistence.*

@Entity
@Table(name = "agent")
class Agent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var id_token: String,

    var refreshToken: String
)