package gg.match.domain.board.overwatch.repository

import gg.match.domain.board.overwatch.entity.Hero
import gg.match.domain.board.overwatch.entity.Type
import org.springframework.data.jpa.repository.JpaRepository

interface HeroRepository: JpaRepository<Hero, Long> {
    fun findByNameAndBattletagAndType(name: String, battletag: Long, type: Type): Hero
    fun findByNameAndType(name: String, type: Type): Hero
    fun existsByNameAndBattletag(name: String, battletag: Long): Boolean
    fun deleteByNameAndBattletag(name: String, battletag: Long)
}