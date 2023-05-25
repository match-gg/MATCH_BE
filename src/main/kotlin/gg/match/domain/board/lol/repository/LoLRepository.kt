package gg.match.domain.board.lol.repository

import gg.match.domain.board.lol.entity.LoL
import gg.match.domain.board.lol.entity.Position
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface LoLRepository: JpaRepository<LoL, Long> {
    fun findAllByOrderByIdDesc(pageable: Pageable): Page<LoL>
    fun findAllByPositionOrderByIdDesc(pageable: Pageable, position: Position): Page<LoL>
    fun findAllByTypeOrderByIdDesc(pageable: Pageable, type: Type): Page<LoL>
    fun findAllByTierOrderByIdDesc(pageable: Pageable, tier: Tier): Page<LoL>
    fun findAllByPositionAndTypeOrderByIdDesc(pageable: Pageable, position: Position, type: Type): Page<LoL>
    fun findAllByTypeAndTierOrderByIdDesc(pageable: Pageable, type: Type, tier: Tier): Page<LoL>
    fun findAllByPositionAndTierOrderByIdDesc(pageable: Pageable, position: Position, tier: Tier): Page<LoL>
    fun findAllByPositionAndTypeAndTierOrderByIdDesc(pageable: Pageable, position: Position, type: Type, tier: Tier): Page<LoL>
}