package gg.match.domain.board.valorant.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.valorant.entity.AgentByMatch
import gg.match.domain.board.valorant.entity.ValorantGameModes

@JsonIgnoreProperties(ignoreUnknown = true)
data class AgentByMatchDTO (
    @JsonProperty("matchId")
    var matchId: String,

    @JsonProperty("agentName")
    var agentName: String,

    @JsonProperty("character_name")
    var characterName: String,

    @JsonProperty("gameMode")
    var gameMode: ValorantGameModes,

    @JsonProperty("avgDmg")
    var avgDmg: Long,

    @JsonProperty("head")
    var head: Long,

    @JsonProperty("shots")
    var shots: Long,

    @JsonProperty("kills")
    var kills: Long,

    @JsonProperty("deaths")
    var deaths: Long,

    @JsonProperty("won")
    var won: String,

    @JsonProperty("tier")
    var tier: Long,

    @JsonProperty("isRanked")
    var isRanked: String
){
    fun toEntity(valorantUserName: String): AgentByMatch{
        return AgentByMatch(
            puuid = valorantUserName,
            matchId = matchId,
            agentName = agentName,
            characterName = characterName,
            gameMode = gameMode,
            avgDmg = avgDmg,
            head = head,
            shots = shots,
            kills = kills,
            deaths = deaths,
            won = won,
            tier = tier,
            isRanked = isRanked
        )
    }
}