package gg.match.domain.board.valorant.repository

import gg.match.domain.board.valorant.entity.Valorant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

interface ValorantRepository: JpaRepository<Valorant, Long> {
    fun findAllByOrderByExpiredAscIdDesc(pageable: Pageable): Page<Valorant>
    fun findAllByValorantGameModesOrderByExpiredAscIdDesc(pageable: Pageable, valorantGameModes: String): Page<Valorant>
    fun findAllByOauth2IdInAndExpiredAndFinishedOrderByIdDesc(pageable: Pageable, @Param("oauth2Ids") oauth2Ids: List<String>, expired: String, finished: String): Page<Valorant>
}