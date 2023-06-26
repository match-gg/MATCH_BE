package gg.match.domain.board.pubg.repository

import gg.match.domain.board.pubg.entity.Pubg
import org.springframework.data.jpa.repository.JpaRepository

interface PubgRepository: JpaRepository<Pubg, Long> {
}