package gg.match.domain.board.pubg.entity

import gg.match.domain.board.pubg.dto.PlayerResponseDTO
import javax.persistence.*

@Entity
@Table(name = "summoner")
class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var tier: Tier
) {
    fun toPlayerResponseDTO(): PlayerResponseDTO {
        return PlayerResponseDTO(
            tier = tier
        )
    }
}