package gg.match.domain.board.lol.entity

import gg.match.domain.board.lol.dto.SummonerResponseDTO
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
) {
    fun toSummonerResponseDTO(): SummonerResponseDTO{
        return SummonerResponseDTO(
            queueType = queueType,
            summonerName = summonerName,
            tier = tier,
            rank = rank,
            leaguePoints = leaguePoints,
            wins = wins,
            losses = losses,
            mostChampion = listOf(most1Champion, most2Champion, most3Champion)
        )
    }

    fun update(most1Champion: String, most2Champion: String, most3Champion: String){
        this.most1Champion = most1Champion
        this.most2Champion = most2Champion
        this.most3Champion = most3Champion
    }
}