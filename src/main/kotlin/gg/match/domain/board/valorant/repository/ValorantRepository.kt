package gg.match.domain.board.valorant.repository

import gg.match.domain.board.valorant.entity.Valorant
import gg.match.domain.board.valorant.entity.ValorantGameModes
import gg.match.domain.board.valorant.entity.ValorantPosition
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

interface ValorantRepository: JpaRepository<Valorant, Long> {
    fun findAllByOrderByExpiredAscIdDesc(pageable: Pageable): Page<Valorant>
    fun findAllByValorantGameModesOrderByExpiredAscIdDesc(pageable: Pageable, valorantGameModes: ValorantGameModes): Page<Valorant>
    fun findAllByValorantGameModesAndTierOrderByExpiredAscIdDesc(pageable: Pageable, valorantGameModes: ValorantGameModes, tier: Long): Page<Valorant>
    fun findAllByValorantGameModesAndPositionOrderByExpiredAscIdDesc(pageable: Pageable, valorantGameModes: ValorantGameModes, position: ValorantPosition): Page<Valorant>
    fun findAllByValorantGameModesAndPositionAndTierOrderByExpiredAscIdDesc(pageable: Pageable, valorantGameModes: ValorantGameModes, position: ValorantPosition, tier: Long): Page<Valorant>
    fun findAllByTierOrderByExpiredAscIdDesc(pageable: Pageable, tier: Long): Page<Valorant>
    fun findAllByPositionOrderByExpiredAscIdDesc(pageable: Pageable, position: ValorantPosition): Page<Valorant>
    fun findAllByPositionAndTierOrderByExpiredAscIdDesc(pageable: Pageable, position: ValorantPosition, tier: Long): Page<Valorant>


    fun findAllByOauth2IdInAndExpiredAndFinishedOrderByIdDesc(pageable: Pageable, @Param("oauth2Ids") oauth2Ids: List<String>, expired: String, finished: String): Page<Valorant>
}