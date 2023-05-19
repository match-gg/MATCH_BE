package gg.match.domain.board.lol.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.lol.entity.ChampionByMatch

@JsonIgnoreProperties(ignoreUnknown = true)
class ChampionByMatchDTO (
    @JsonProperty("summonerName")
    var summonerName: String,

    @JsonProperty("champion")
    var champion: String,

    @JsonProperty("matchId")
    var matchId: String,

    @JsonProperty
    var lane: String
) {
    fun toEntity(): ChampionByMatch{
        return ChampionByMatch(
            summonerName = summonerName,
            champion = champion,
            matchId = matchId,
            lane = lane
        )
    }
}