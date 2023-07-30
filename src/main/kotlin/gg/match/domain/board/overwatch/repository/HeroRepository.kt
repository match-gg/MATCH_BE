package gg.match.domain.board.overwatch.repository

import gg.match.domain.board.overwatch.entity.Hero
import gg.match.domain.board.overwatch.entity.Type
import org.springframework.data.jpa.repository.JpaRepository

interface HeroRepository: JpaRepository<Hero, Long> {
    fun findByNameAndType(name: String, type: Type): Hero
    fun existsByName(name: String): Boolean
    fun deleteByName(name: String)
}