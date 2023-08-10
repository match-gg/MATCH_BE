package gg.match.domain.board.pubg.entity

import javax.persistence.*

@Entity
@Table(name = "player")
class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Enumerated(EnumType.STRING)
    var platform: Platform,

    var name: String,

    var tier: String,

    var subTier: String,

    var currentRankPoint: Int,

    @Enumerated(EnumType.STRING)
    var type: Type,

    var kills: Int,

    var deaths: Int,

    var avgDmg: Float,

    var totalPlayed: Int,

    var wins: Int,

    var top10: Int
)