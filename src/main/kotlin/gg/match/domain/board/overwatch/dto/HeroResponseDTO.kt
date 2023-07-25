package gg.match.domain.board.overwatch.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.overwatch.entity.Type

data class HeroResponseDTO(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("battletag")
    val battletag: Long,

    @JsonProperty("type")
    val type: Type,

    @JsonProperty("tank_tier")
    val tank_tier: String,

    @JsonProperty("tank_rank")
    val tank_rank: String,

    @JsonProperty("damage_tier")
    val damage_tier: String,

    @JsonProperty("damage_rank")
    val damage_rank: String,

    @JsonProperty("support_tier")
    val support_tier: String,

    @JsonProperty("support_rank")
    val support_rank: String,

    @JsonProperty("wins")
    val wins: Long,

    @JsonProperty("losses")
    val losses: Long,

    @JsonProperty("kills")
    val kills: Long,

    @JsonProperty("deaths")
    val deaths: Long,

    @JsonProperty("mostHero")
    var mostHero: List<String>
)