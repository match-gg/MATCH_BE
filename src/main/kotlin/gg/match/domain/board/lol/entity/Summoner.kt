package gg.match.domain.board.lol.entity

import javax.persistence.*

@Entity
@Table(name = "summoner")
class Summoner(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var queueType: String,

    var summonerName: String,

    var tier: String,

    var rank: String,

    var leaguePoints: Int,

    var wins: Int,

    var losses: Int,

    var most1Champion: String = "garen",

    var most2Champion: String = "galio",

    var most3Champion: String = "lux"
)