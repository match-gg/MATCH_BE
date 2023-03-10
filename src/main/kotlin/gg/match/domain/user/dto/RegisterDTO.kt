package gg.match.domain.user.dto

import com.fasterxml.jackson.annotation.JsonProperty
import gg.match.domain.user.entity.Game

data class RegisterDTO (
    @JsonProperty("representative")
    val representative: Game,

    @JsonProperty("lol")
    val lol: String?,

    @JsonProperty("overwatch")
    val overwatch: String?,

    @JsonProperty("pubg")
    val pubg: String?,

    @JsonProperty("maplestory")
    val maplestory: String?,

    @JsonProperty("lostark")
    val lostark: String?
)