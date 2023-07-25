package gg.match.domain.board.overwatch.repository

import gg.match.domain.board.overwatch.entity.Position
import gg.match.domain.board.overwatch.entity.Tier
import gg.match.domain.board.overwatch.entity.Type
import gg.match.domain.board.overwatch.entity.Overwatch
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface OverwatchRepository: JpaRepository<Overwatch, Long> {
    fun findAllByOrderByExpiredAscIdDesc(pageable: Pageable): Page<Overwatch>
    fun findAllByPositionOrderByExpiredAscIdDesc(pageable: Pageable, position: Position): Page<Overwatch>
    fun findAllByTypeOrderByExpiredAscIdDesc(pageable: Pageable, type: Type): Page<Overwatch>
    fun findAllByTierOrderByExpiredAscIdDesc(pageable: Pageable, tier: Tier): Page<Overwatch>
    fun findAllByPositionAndTypeOrderByExpiredAscIdDesc(pageable: Pageable, position: Position, type: Type): Page<Overwatch>
    fun findAllByTypeAndTierOrderByExpiredAscIdDesc(pageable: Pageable, type: Type, tier: Tier): Page<Overwatch>
    fun findAllByPositionAndTierOrderByExpiredAscIdDesc(pageable: Pageable, position: Position, tier: Tier): Page<Overwatch>
    fun findAllByPositionAndTypeAndTierOrderByExpiredAscIdDesc(pageable: Pageable, position: Position, type: Type, tier: Tier): Page<Overwatch>
}