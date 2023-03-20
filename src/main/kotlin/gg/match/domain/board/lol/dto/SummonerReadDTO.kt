package gg.match.domain.board.lol.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.lol.entity.Tier

class SummonerReadDTO (
    @JsonProperty("id")
    var id: Long,

    @JsonProperty("name")
    var nickname: String,

    @JsonProperty("tier")
    var tier: Tier,

    @JsonProperty("rank")
    var rank: Int,

    @JsonProperty("leaguePoint")
    var leaguePoint: Int,

    @JsonProperty("wins")
    var wins: Int,

    @JsonProperty("losses")
    var losses: Int,

    @JsonProperty("most1Champion")
    var most1Champion: String,

    @JsonProperty("most2Champion")
    var most2Champion: String,

    @JsonProperty("most3Champion")
    var most3Champion: String
)