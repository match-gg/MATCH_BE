package gg.match.domain.board.lol.entity

import gg.match.domain.board.lol.dto.SummonerReadDTO
import javax.persistence.*

@Entity
@Table(name = "summoner")
class Summoner(
    //티어, 모스트 3 챔피언, 승률, 주포지션
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var nickname: String,

    @Enumerated(EnumType.STRING)
    var tier: Tier,

    var rank: Int,

    var leaguePoint: Int,

    var wins: Int,

    var losses: Int,

    var most1Champion: String,

    var most2Champion: String,

    var most3Champion: String
){
    fun toSummonerReadDTO(): SummonerReadDTO{
        return SummonerReadDTO(
            id = id,
            nickname = nickname,
            tier = tier,
            rank = rank,
            leaguePoint = leaguePoint,
            wins = wins,
            losses = losses,
            most1Champion = most1Champion,
            most2Champion = most2Champion,
            most3Champion = most3Champion
        )
    }

//    fun update(lolRequestDTO: LoLRequestDTO){
//        name = lolRequestDTO.name
//        type = lolRequestDTO.type
//        tier = lolRequestDTO.tier
//        position = lolRequestDTO.position
//        voice = lolRequestDTO.voice
//        content = lolRequestDTO.content
//        expire = lolRequestDTO.expire
//    }
}