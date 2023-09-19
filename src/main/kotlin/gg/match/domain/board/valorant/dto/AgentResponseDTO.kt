package gg.match.domain.board.valorant.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AgentResponseDTO (

    @JsonProperty("puuid")
    val puuid: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("tier")
    val tier: Long,

    @JsonProperty("wins")
    val wins: Long,

    @JsonProperty("losses")
    val losses: Long,

    @JsonProperty("kills")
    val kills: Long,

    @JsonProperty("deaths")
    val deaths: Long,

    @JsonProperty("avgDmg")
    val avgDmg: Long,

    @JsonProperty("heads")
    val heads: Long,

    @JsonProperty("shots")
    val shots: Long,

    @JsonProperty("mostAgent")
    var mostAgent: List<String>
)