package gg.match.domain.board.overwatch.repository

import gg.match.domain.board.overwatch.entity.Position
import gg.match.domain.board.overwatch.entity.Tier
import gg.match.domain.board.overwatch.entity.Type
import gg.match.domain.board.overwatch.entity.Overwatch
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface OverwatchRepository: JpaRepository<Overwatch, Long> {
    fun findAllByOrderByIdDesc(pageable: Pageable): Page<Overwatch>
    fun findAllByPositionOrderByIdDesc(pageable: Pageable, position: Position): Page<Overwatch>
    fun findAllByTypeOrderByIdDesc(pageable: Pageable, type: Type): Page<Overwatch>
    fun findAllByTierOrderByIdDesc(pageable: Pageable, tier: Tier): Page<Overwatch>
    fun findAllByPositionAndTypeOrderByIdDesc(pageable: Pageable, position: Position, type: Type): Page<Overwatch>
    fun findAllByTypeAndTierOrderByIdDesc(pageable: Pageable, type: Type, tier: Tier): Page<Overwatch>
    fun findAllByPositionAndTierOrderByIdDesc(pageable: Pageable, position: Position, tier: Tier): Page<Overwatch>
    fun findAllByPositionAndTypeAndTierOrderByIdDesc(pageable: Pageable, position: Position, type: Type, tier: Tier): Page<Overwatch>
}