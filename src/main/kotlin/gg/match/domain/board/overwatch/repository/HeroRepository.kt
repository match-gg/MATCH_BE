package gg.match.domain.board.overwatch.repository

import gg.match.domain.board.overwatch.entity.Hero
import gg.match.domain.board.overwatch.entity.Type
import org.springframework.data.jpa.repository.JpaRepository

interface HeroRepository: JpaRepository<Hero, Long> {
    fun findByNameAndBattletagAndType(name: String, battletag: Int, type: Type): Hero
    fun findByName(name: String): Hero
}