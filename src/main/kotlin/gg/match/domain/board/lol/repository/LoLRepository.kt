package gg.match.domain.board.lol.repository

import gg.match.domain.board.lol.entity.LoL
import gg.match.domain.board.lol.entity.Position
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LoLRepository: JpaRepository<LoL, Long> {
    fun findByPositionAndTypeAndTier(pageable: Pageable, position: Position, type: Type, tier: Tier): Page<LoL>
    fun findByPositionAndTier(pageable: Pageable, position: Position, tier: Tier): Page<LoL>
}