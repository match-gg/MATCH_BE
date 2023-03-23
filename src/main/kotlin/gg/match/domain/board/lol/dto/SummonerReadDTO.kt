package gg.match.domain.board.lol.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.lol.entity.Summoner

class SummonerReadDTO (
    @JsonProperty("id")
    var id: Long,

    @JsonProperty("leagueId")
    var leagueId: String,

    @JsonProperty("summonerId")
    var summonerId: String,

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
    var losses: Int,

    @JsonProperty("hotStreak")
    var hotStreak: Boolean,

    @JsonProperty("veteran")
    var veteran: Boolean,

    @JsonProperty("freshBlood")
    var freshBlood: Boolean,

    @JsonProperty("inactive")
    var inactive: Boolean
) {
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