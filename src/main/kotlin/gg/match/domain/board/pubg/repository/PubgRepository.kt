package gg.match.domain.board.pubg.repository

import gg.match.domain.board.pubg.entity.Platform
import gg.match.domain.board.pubg.entity.Tier
import gg.match.domain.board.pubg.entity.Type
import gg.match.domain.board.pubg.entity.Pubg
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PubgRepository: JpaRepository<Pubg, Long> {
    fun findAllByOrderByIdDesc(pageable: Pageable): Page<Pubg>
    fun findAllByPlatformOrderByIdDesc(pageable: Pageable, platform: Platform): Page<Pubg>
    fun findAllByTypeOrderByIdDesc(pageable: Pageable, type: Type): Page<Pubg>
    fun findAllByTierOrderByIdDesc(pageable: Pageable, tier: Tier): Page<Pubg>
    fun findAllByPlatformAndTypeOrderByIdDesc(pageable: Pageable, platform: Platform, type: Type): Page<Pubg>
    fun findAllByTypeAndTierOrderByIdDesc(pageable: Pageable, type: Type, tier: Tier): Page<Pubg>
    fun findAllByPlatformAndTierOrderByIdDesc(pageable: Pageable, platform: Platform, tier: Tier): Page<Pubg>
    fun findAllByPlatformAndTypeAndTierOrderByIdDesc(pageable: Pageable, platform: Platform, type: Type, tier: Tier): Page<Pubg>
}