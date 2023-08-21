package gg.match.domain.board.overwatch.entity

import gg.match.domain.board.overwatch.dto.HeroResponseDTO
import javax.persistence.*

@Entity
@Table(name = "hero")
class Hero(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var name: String,

    @Enumerated(EnumType.STRING)
    var type: Type,

    var tank_tier: String = "none",

    var tank_rank: String = "none",

    var damage_tier: String = "none",

    var damage_rank: String = "none",

    var support_tier: String = "none",

    var support_rank: String = "none",

    var wins: Long = 0L,

    var losses: Long = 0L,

    var kills: Long = 0L,

    var deaths: Long = 0L,

    var most1Hero: String = "poro",

    var most2Hero: String = "poro",

    var most3Hero: String = "poro",
) {
    fun toHeroResponseDTO(): HeroResponseDTO{
        return HeroResponseDTO(
            name = name,
            type = type,
            tank_tier = tank_tier,
            tank_rank = tank_rank,
            damage_tier = damage_tier,
            damage_rank = damage_rank,
            support_tier = support_tier,
            support_rank = support_rank,
            wins = wins,
            losses = losses,
            kills = kills,
            deaths = deaths,
            mostHero = listOf(most1Hero, most2Hero, most3Hero)
        )
    }
}