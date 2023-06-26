package gg.match.domain.board.pubg.repository

import gg.match.domain.board.pubg.entity.Player
import gg.match.domain.board.pubg.entity.Tier
import org.springframework.data.jpa.repository.JpaRepository

interface PlayerRepository: JpaRepository<Player, Long> {
    fun findByIdAndTier(id: Long, tier: Tier): Player
}