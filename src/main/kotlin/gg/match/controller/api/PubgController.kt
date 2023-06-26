package gg.match.controller.api

import gg.match.controller.common.dto.PageResult
import gg.match.domain.board.pubg.entity.Tier
import gg.match.domain.board.pubg.entity.Type
import gg.match.domain.board.pubg.dto.ReadPubgBoardDTO
import gg.match.domain.board.pubg.entity.Platform
import gg.match.domain.board.pubg.service.PubgService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/pubg")
class PubgController (
    private val pubgService: PubgService
){
    @GetMapping("/boards")
    fun getBoards(
        @PageableDefault(size = 10) pageable: Pageable,
        @RequestParam(required = false, defaultValue = "ALL") platform: Platform,
        @RequestParam(required = false, defaultValue = "ALL") type: Type,
        @RequestParam(required = false, defaultValue = "ALL") tier: Tier
    ): PageResult<ReadPubgBoardDTO> {
        return pubgService.getBoards(pageable, platform, type, tier)
    }

/*
    fun voiceUpper(voice: String): String{
        return voice.uppercase()
    }
*/
}