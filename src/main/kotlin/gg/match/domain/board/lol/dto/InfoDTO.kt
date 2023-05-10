package gg.match.domain.board.lol.dto

data class InfoDTO (
    val participants: List<ParticipantDto>,
    val gameMode: String
)