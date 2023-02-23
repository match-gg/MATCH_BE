package gg.match.domain.board.lol.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.controller.common.entity.Expire
import gg.match.domain.board.lol.entity.Position
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type

data class LoLRequestDTO (
    @JsonProperty("content")
    val content: String,
)