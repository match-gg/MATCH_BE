package gg.match.domain.board.lol.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SummonerResponseDTO(
    @JsonProperty("queueType")
    val queueType: String,

    @JsonProperty("summonerName")
    val summonerName: String,

    @JsonProperty("tier")
    val tier: String,

    @JsonProperty("rank")
    val rank: String,

    @JsonProperty("leaguePoints")
    var leaguePoints: Int,

    @JsonProperty("wins")
    val wins: Int,

    @JsonProperty("losses")
    val losses: Int,

    @JsonProperty("mostChampion")
    var mostChampion: List<String>
)