package gg.match.domain.board.valorant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.valorant.entity.Agent
import gg.match.domain.board.valorant.entity.ValorantCharacters
import gg.match.domain.board.valorant.entity.ValorantGameModes

data class AgentReadDTO (
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

    @JsonProperty("looses")
    var looses: Long,

    @JsonProperty("heads")
    var heads: Long,

    @JsonProperty("shots")
    var shots: Long,

    @JsonProperty("most1Agent")
    var most1Agent: ValorantCharacters,

    @JsonProperty("most2Agent")
    var most2Agent: ValorantCharacters,

    @JsonProperty("most3Agent")
    var most3Agent: ValorantCharacters,
){
    fun toEntity(agent: Agent): Agent{
        return Agent(
            agentName = agent.agentName,
            puuid = agent.puuid,
            id_token = agent.id_token,
            refreshToken = agent.refreshToken,
            gameMode = gameMode,
            tier = tier,
            avgDmg = avgDmg,
            kills = kills,
            deaths = deaths,
            wins = wins,
            looses = looses,
            heads = heads,
            shots = shots,
            most1Agent = most1Agent,
            most2Agent = most2Agent,
            most3Agent = most3Agent
        )
    }
}
