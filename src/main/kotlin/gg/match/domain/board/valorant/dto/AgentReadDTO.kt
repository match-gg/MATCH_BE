package gg.match.domain.board.valorant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.valorant.entity.Agent
import gg.match.domain.board.valorant.entity.ValorantGameModes

data class AgentReadDTO (

    @JsonProperty("name")
    var name: String,

    @JsonProperty("puuid")
    var puuid: String,

    @JsonProperty("id_token")
    var idToken: String,

    @JsonProperty("refresh_token")
    var refreshToken: String,

    @JsonProperty("gameMode")
    var gameMode: ValorantGameModes,

    @JsonProperty("tier")
    var tier: Long,

    @JsonProperty("avgDmg")
    var avgDmg: Long,

    @JsonProperty("kills")
    var kills: Long,

    @JsonProperty("deaths")
    var deaths: Long,

    @JsonProperty("wins")
    var wins: Long,

    @JsonProperty("losses")
    var losses: Long,

    @JsonProperty("heads")
    var heads: Long,

    @JsonProperty("shots")
    var shots: Long,

    @JsonProperty("most1Agent")
    var most1Agent: String,

    @JsonProperty("most2Agent")
    var most2Agent: String,

    @JsonProperty("most3Agent")
    var most3Agent: String,
){
    fun toEntity(): Agent{
        return Agent(
            name = name,
            puuid = puuid,
            id_token = idToken,
            refreshToken = refreshToken,
            gameMode = gameMode,
            tier = tier,
            avgDmg = avgDmg,
            kills = kills,
            deaths = deaths,
            wins = wins,
            losses = losses,
            heads = heads,
            shots = shots,
            most1Agent = most1Agent,
            most2Agent = most2Agent,
            most3Agent = most3Agent
        )
    }
}
