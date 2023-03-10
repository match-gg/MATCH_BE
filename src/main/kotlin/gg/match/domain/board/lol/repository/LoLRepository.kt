package gg.match.domain.board.lol.repository

import gg.match.domain.board.lol.entity.LoL
import org.springframework.data.jpa.repository.JpaRepository

interface LoLRepository: JpaRepository<LoL, Long> {
}