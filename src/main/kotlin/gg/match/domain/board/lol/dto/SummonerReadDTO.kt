package gg.match.domain.board.lol.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.lol.entity.Summoner

@JsonIgnoreProperties(ignoreUnknown = true)
class SummonerReadDTO (
    @JsonProperty("summonerName")
    var summonerName: String,

    @JsonProperty("queueType")
    var queueType: String,

    @JsonProperty("tier")
    var tier: String,

    @JsonProperty("rank")
    var rank: String,

    @JsonProperty("leaguePoints")
    var leaguePoints: Int,

    @JsonProperty("wins")
    var wins: Int,

    @JsonProperty("losses")
    var losses: Int
) {
    fun makeUnRankedSummoner(): Summoner{
        return Summoner(
            queueType = "None",
            summonerName = summonerName,
            tier = "UNRANKED",
            rank = "UNRANKED",
            leaguePoints = 0,
            wins = 0,
            losses = 0
        )
    }
    fun toEntity(): Summoner{
        return Summoner(
            queueType = queueType,
            summonerName = summonerName,
            tier = tier,
            rank = rank,
            leaguePoints = leaguePoints,
            wins = wins,
            losses = losses
        )
    }
}