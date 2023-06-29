package gg.match.domain.board.pubg.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.pubg.entity.Platform
import gg.match.domain.board.pubg.entity.Type

data class PlayerResponseDTO(
    @JsonProperty("id")
    var id: Long = 0,

    @JsonProperty("name")
    var name: String,

    @JsonProperty("platform")
    var platform: Platform,

    @JsonProperty("type")
    var type: Type,

    @JsonProperty("currentRankPoint")
    var currentRankPoint: Int,

    @JsonProperty("tier")
    var tier: String,

    @JsonProperty("subTier")
    var subTier: String,

    @JsonProperty("kills")
    var kills: Int,

    @JsonProperty("deaths")
    var deaths: Int,

    @JsonProperty("avgDmg")
    var avgDmg: Float,

    @JsonProperty("totalPlayed")
    var totalPlayed: Int,

    @JsonProperty("wins")
    var wins: Int,

    @JsonProperty("top10")
    var top10: Int
)