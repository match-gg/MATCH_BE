package gg.match.domain.board.lol.repository

import gg.match.domain.board.lol.entity.LoL
import gg.match.domain.board.lol.entity.Position
import gg.match.domain.board.lol.entity.Tier
import gg.match.domain.board.lol.entity.Type
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

interface LoLRepository: JpaRepository<LoL, Long> {
    fun findAllByOrderByExpiredAscIdDesc(pageable: Pageable): Page<LoL>
    fun findAllByPositionOrderByExpiredAscIdDesc(pageable: Pageable, position: Position): Page<LoL>
    fun findAllByTypeOrderByExpiredAscIdDesc(pageable: Pageable, type: Type): Page<LoL>
    fun findAllByTierOrderByExpiredAscIdDesc(pageable: Pageable, tier: Tier): Page<LoL>
    fun findAllByPositionAndTypeOrderByExpiredAscIdDesc(pageable: Pageable, position: Position, type: Type): Page<LoL>
    fun findAllByTypeAndTierOrderByExpiredAscIdDesc(pageable: Pageable, type: Type, tier: Tier): Page<LoL>
    fun findAllByPositionAndTierOrderByExpiredAscIdDesc(pageable: Pageable, position: Position, tier: Tier): Page<LoL>
    fun findAllByPositionAndTypeAndTierOrderByExpiredAscIdDesc(pageable: Pageable, position: Position, type: Type, tier: Tier): Page<LoL>

    fun findAllByOauth2IdInAndExpiredAndFinishedOrderByIdDesc(pageable: Pageable, @Param("oauth2Ids") oauth2Ids: List<String>, expired: String, finished: String): Page<LoL>
}