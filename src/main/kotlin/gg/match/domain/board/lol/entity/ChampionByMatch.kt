package gg.match.domain.board.lol.entity

import javax.persistence.*

@Entity
@Table(name = "championByMatch")
class ChampionByMatch(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var summonerName: String,

    var champion: String,

    var matchId: String,

    var mostLane: String
)