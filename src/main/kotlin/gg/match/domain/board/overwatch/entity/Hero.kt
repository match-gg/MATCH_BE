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

    var battletag: Long,

    var type: Type,

    var tank_tier: String = "none",

    var tank_rank: String = "none",

    var damage_tier: String = "none",

    var damage_rank: String = "none",

    var support_tier: String = "none",

    var support_rank: String = "none",

    var wins: Long,

    var losses: Long,

    var kills: Long,

    var deaths: Long,

    var most1Hero: String = "none",

    var most2Hero: String = "none",

    var most3Hero: String = "none",
) {
    fun toHeroResponseDTO(): HeroResponseDTO{
        return HeroResponseDTO(
            name = name,
            battletag = battletag,
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

    fun updateHero(mostHeroList: List<Pair<String, Int>>){
        when(mostHeroList.size){
            0 -> {
                most1Hero = "none"
                most2Hero = "none"
                most3Hero = "none"
            }
            1 -> {
                most1Hero = mostHeroList[0].first
                most2Hero = "none"
                most3Hero = "none"
            }
            2 -> {
                most1Hero = mostHeroList[0].first
                most2Hero = mostHeroList[1].first
                most3Hero = "none"
            }
            else -> {
                most1Hero = mostHeroList[0].first
                most2Hero = mostHeroList[1].first
                most3Hero = mostHeroList[2].first
            }
        }
    }
}