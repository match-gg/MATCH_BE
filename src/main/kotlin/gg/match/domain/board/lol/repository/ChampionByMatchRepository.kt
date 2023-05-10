package gg.match.domain.board.lol.repository

import gg.match.domain.board.lol.dto.Champion
import gg.match.domain.board.lol.entity.ChampionByMatch
import org.springframework.data.jpa.repository.JpaRepository

interface ChampionByMatchRepository: JpaRepository<ChampionByMatch, Long> {
    fun existsByMatchIdAndSummonerName(matchId: String, summonerName: String): Boolean
    fun findAllBySummonerName(summonerName: String): List<Champion>
}