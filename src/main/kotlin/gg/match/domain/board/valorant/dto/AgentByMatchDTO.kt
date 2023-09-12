package gg.match.domain.board.valorant.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.valorant.entity.AgentByMatch

@JsonIgnoreProperties(ignoreUnknown = true)
data class AgentByMatchDTO (
    @JsonProperty("matchId")
    var matchId: String,

    @JsonProperty("agentName")
    var agentName: String
){
    fun toEntity(valorantUserName: String): AgentByMatch{
        return AgentByMatch(
            puuid = valorantUserName,
            matchId = matchId,
            agentName = agentName
        )
    }
}