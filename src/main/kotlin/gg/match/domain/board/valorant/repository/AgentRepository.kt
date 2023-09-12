package gg.match.domain.board.valorant.repository

import gg.match.domain.board.valorant.entity.Agent
import org.springframework.data.jpa.repository.JpaRepository

interface AgentRepository: JpaRepository<Agent, Long> {

}