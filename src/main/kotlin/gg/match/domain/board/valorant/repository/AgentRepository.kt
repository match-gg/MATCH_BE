package gg.match.domain.board.valorant.repository

import gg.match.domain.board.valorant.entity.Agent
import gg.match.domain.board.valorant.entity.ValorantGameModes
import org.springframework.data.jpa.repository.JpaRepository

interface AgentRepository: JpaRepository<Agent, Long> {
    fun existsByName(name: String): Boolean
    fun findByNameAndGameMode(name: String, gameMode: ValorantGameModes): Agent
    fun deleteAllByPuuid(puuid: String)
    fun findByPuuidAndGameMode(puuid: String, gameMode: ValorantGameModes): Agent
    fun deleteAllByGameModeAndPuuid(gameMode: ValorantGameModes, puuid: String)
}