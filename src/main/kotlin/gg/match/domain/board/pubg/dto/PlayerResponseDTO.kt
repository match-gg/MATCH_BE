package gg.match.domain.board.pubg.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.board.pubg.entity.Tier

data class PlayerResponseDTO(
    @JsonProperty("tier")
    val tier: Tier
)