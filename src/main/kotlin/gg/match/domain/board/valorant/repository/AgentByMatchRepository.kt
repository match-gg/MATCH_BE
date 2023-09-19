package gg.match.domain.board.valorant.repository

import gg.match.domain.board.valorant.entity.AgentByMatch
import gg.match.domain.board.valorant.entity.ValorantGameModes
import org.springframework.data.jpa.repository.JpaRepository

interface AgentByMatchRepository: JpaRepository<AgentByMatch, Long> {
    fun existsByMatchId(matchId: String): Boolean
    fun countByGameMode(gameMode: String): Long
    fun findAllByGameMode(gameMode: String): List<AgentByMatch>?
}