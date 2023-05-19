package gg.match.domain.board.lol.repository

import gg.match.domain.board.lol.entity.LoL
import gg.match.domain.board.lol.entity.Position
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LoLRepository: JpaRepository<LoL, Long> {
    override fun findAll(pageable: Pageable): Page<LoL>
    fun findAllByPosition(pageable: Pageable, position: Position): Page<LoL>
    fun findAllByType(pageable: Pageable, type: Type): Page<LoL>
    fun findAllByTier(pageable: Pageable, tier: Tier): Page<LoL>
    fun findAllByPositionAndType(pageable: Pageable, position: Position, type: Type): Page<LoL>
    fun findAllByTypeAndTier(pageable: Pageable, type: Type, tier: Tier): Page<LoL>
    fun findAllByPositionAndTier(pageable: Pageable, position: Position, tier: Tier): Page<LoL>
    fun findAllByPositionAndTypeAndTier(pageable: Pageable, position: Position, type: Type, tier: Tier): Page<LoL>
}