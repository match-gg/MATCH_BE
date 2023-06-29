package gg.match.domain.board.pubg.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.pubg.entity.Platform
import gg.match.domain.board.pubg.entity.Player
import gg.match.domain.board.pubg.entity.Type
import javax.persistence.Column

@JsonIgnoreProperties(ignoreUnknown=true)
data class PlayerReadDTO(
    @JsonProperty("tier")
    var tier: String,

    @JsonProperty("subTier")
    var subTier: String,

    @JsonProperty("currentRankPoint")
    var currentRankPoint: Int,

    @JsonProperty("kills")
    var kills: Int,

    @JsonProperty("avgDmg")
    var damageDealt: Float,

    @JsonProperty("totalPlayed")
    var roundsPlayed: Int,

    @JsonProperty("wins")
    var wins: Int,

    @JsonProperty("top10")
    var top10s: Int
){
    fun toEntity(name: String, platform: Platform, type: Type): Player {
        return if(roundsPlayed !=0) {
            Player(
                platform = platform,
                name = name,
                type = type,
                tier = tier,
                subTier = subTier,
                currentRankPoint = currentRankPoint,
                kills = kills,
                deaths = roundsPlayed - wins,
                avgDmg = damageDealt / roundsPlayed,
                totalPlayed = roundsPlayed,
                wins = wins,
                top10 = top10s
            )
        }
        else{
            Player(
                platform = platform,
                name = name,
                type = type,
                tier = tier,
                subTier = subTier,
                currentRankPoint = currentRankPoint,
                kills = 0,
                deaths = 0,
                avgDmg = 0F,
                totalPlayed = 0,
                wins = 0,
                top10 = 0
            )
        }
    }
}