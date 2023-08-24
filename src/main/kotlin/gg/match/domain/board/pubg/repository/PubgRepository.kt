package gg.match.domain.board.pubg.repository

import gg.match.domain.board.pubg.entity.Platform
import gg.match.domain.board.pubg.entity.Tier
import gg.match.domain.board.pubg.entity.Type
import gg.match.domain.board.pubg.entity.Pubg
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

interface PubgRepository: JpaRepository<Pubg, Long> {
    fun findAllByOrderByExpiredAscIdDesc(pageable: Pageable): Page<Pubg>
    fun findAllByPlatformOrderByExpiredAscIdDesc(pageable: Pageable, platform: Platform): Page<Pubg>
    fun findAllByTypeOrderByExpiredAscIdDesc(pageable: Pageable, type: Type): Page<Pubg>
    fun findAllByTierOrderByExpiredAscIdDesc(pageable: Pageable, tier: Tier): Page<Pubg>
    fun findAllByPlatformAndTypeOrderByExpiredAscIdDesc(pageable: Pageable, platform: Platform, type: Type): Page<Pubg>
    fun findAllByTypeAndTierOrderByExpiredAscIdDesc(pageable: Pageable, type: Type, tier: Tier): Page<Pubg>
    fun findAllByPlatformAndTierOrderByExpiredAscIdDesc(pageable: Pageable, platform: Platform, tier: Tier): Page<Pubg>
    fun findAllByPlatformAndTypeAndTierOrderByExpiredAscIdDesc(pageable: Pageable, platform: Platform, type: Type, tier: Tier): Page<Pubg>

    fun findAllByOauth2IdInAndExpiredAndFinishedOrderByIdDesc(pageable: Pageable, @Param("oauth2Ids") oauth2Ids: List<String>, expired: String, finished: String): Page<Pubg>
}