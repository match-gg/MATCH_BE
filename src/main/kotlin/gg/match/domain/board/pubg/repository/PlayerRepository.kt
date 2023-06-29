package gg.match.domain.board.pubg.repository

import gg.match.domain.board.pubg.dto.PlayerResponseDTO
import gg.match.domain.board.pubg.entity.Platform
import gg.match.domain.board.pubg.entity.Player
import gg.match.domain.board.pubg.entity.Tier
import gg.match.domain.board.pubg.entity.Type
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerRepository: JpaRepository<Player, Long> {
    fun existsByName(name: String): Boolean
    fun deleteAllByName(name: String)
    fun findByNameAndPlatformAndType(name: String, platform: Platform, type: Type): PlayerResponseDTO
}