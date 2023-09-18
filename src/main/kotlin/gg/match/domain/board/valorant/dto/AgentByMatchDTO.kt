package gg.match.domain.board.valorant.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.valorant.entity.AgentByMatch

@JsonIgnoreProperties(ignoreUnknown = true)
data class AgentByMatchDTO (
    @JsonProperty("matchId")
    var matchId: String,

    @JsonProperty("agentName")
    var agentName: String,

    @JsonProperty("gameMode")
    var gameMode: String,

    @JsonProperty("avgDmg")
    var avgDmg: Long,

    @JsonProperty("head")
    var head: Long,

    @JsonProperty("shots")
    var shots: Long,

    @JsonProperty("isRanked")
    var isRanked: String
){
    fun toEntity(valorantUserName: String): AgentByMatch{
        return AgentByMatch(
            puuid = valorantUserName,
            matchId = matchId,
            agentName = agentName,
            gameMode = gameMode,
            avgDmg = avgDmg,
            head = head,
            shots = shots,
            isRanked = isRanked
        )
    }
}