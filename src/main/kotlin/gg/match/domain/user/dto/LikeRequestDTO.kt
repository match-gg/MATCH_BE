package gg.match.domain.user.dto

import gg.match.domain.user.entity.Game

data class LikeRequestDTO (
    val game: Game,
    val nickname: String
)