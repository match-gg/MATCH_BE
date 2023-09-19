package gg.match.domain.board.valorant.repository

import gg.match.domain.board.valorant.entity.Valorant
import gg.match.domain.board.valorant.entity.ValorantGameModes
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ValorantRepository: JpaRepository<Valorant, Long> {
    fun findAllByOrderByExpiredAscIdDesc(pageable: Pageable): Page<Valorant>
    fun findAllByValorantGameModesOrderByExpiredAscIdDesc(pageable: Pageable, valorantGameModes: ValorantGameModes): Page<Valorant>
}