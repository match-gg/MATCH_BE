package gg.match.domain.board.lol.repository

import gg.match.domain.board.lol.entity.Summoner
import org.springframework.data.jpa.repository.JpaRepository

interface SummonerRepository: JpaRepository<Summoner, Long> {
}