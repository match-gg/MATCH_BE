package gg.match.domain.board.lol.repository

import gg.match.domain.board.lol.entity.Summoner
import org.springframework.data.jpa.repository.JpaRepository

interface SummonerRepository: JpaRepository<Summoner, Long> {
    fun existsBySummonerName(nickname: String): Boolean
    fun deleteBySummonerName(nickname: String)
    fun findBySummonerNameAndQueueType(summonerName: String, queueType: String): Summoner
    fun countBySummonerName(summonerName: String): Long
    fun findBySummonerName(summonerName: String): Summoner
    fun findAllBySummonerName(summonerName: String): List<Summoner>
}