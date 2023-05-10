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

    var most1Champion: String = "poro",

    var most2Champion: String = "poro",

    var most3Champion: String = "poro"
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

    fun update(mostChampionList: List<Pair<String, Int>>){
        when(mostChampionList.size){
            0 -> {
                most1Champion = "poro"
                most2Champion = "poro"
                most3Champion = "poro"
            }
            1 -> {
                most1Champion = mostChampionList[0].first
                most2Champion = "poro"
                most3Champion = "poro"
            }
            2 -> {
                most1Champion = mostChampionList[0].first
                most2Champion = mostChampionList[1].first
                most3Champion = "poro"
            }
            3 -> {
                most1Champion = mostChampionList[0].first
                most2Champion = mostChampionList[1].first
                most3Champion = mostChampionList[2].first
            }
        }
    }
}