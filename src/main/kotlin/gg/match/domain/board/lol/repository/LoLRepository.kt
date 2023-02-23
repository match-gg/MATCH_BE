package gg.match.domain.board.lol.repository

import gg.match.domain.board.lol.entity.LoL
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LoLRepository: JpaRepository<LoL, Long> {

}