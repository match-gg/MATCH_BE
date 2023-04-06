package gg.match.domain.board.lol.dto

data class InfoDto (
    val participants: List<ParticipantDto>,
    val gameMode: String
)