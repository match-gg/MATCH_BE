package gg.match.domain.board.pubg.service

import gg.match.controller.common.dto.PageResult
import gg.match.controller.common.entity.Expire
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.lol.entity.Summoner
import gg.match.domain.board.pubg.dto.ReadPubgBoardDTO
import gg.match.domain.board.pubg.entity.*
import gg.match.domain.board.pubg.repository.PlayerRepository
import gg.match.domain.board.pubg.repository.PubgRepository
import gg.match.domain.chat.repository.ChatRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class PubgService(
    private val pubgRepository: PubgRepository,
    private val playerRepository: PlayerRepository,
    private val chatRepository: ChatRepository
){
    lateinit var result: PageResult<ReadPubgBoardDTO>

    @Transactional
    fun getBoards(pageable: Pageable, platform: Platform, type: Type, tier: Tier): PageResult<ReadPubgBoardDTO> {
        val boards: Page<Pubg>
        //update expired
        updateExpired()
        //filtering
        if(platform == Platform.valueOf("ALL") && type == Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
            boards = pubgRepository.findAllByOrderByIdDesc(pageable)
        }
        else if(platform == Platform.valueOf("ALL")){
            boards = if(type == Type.valueOf("ALL") && tier != Tier.valueOf("ALL")){
                pubgRepository.findAllByTierOrderByIdDesc(pageable, tier)
            } else if(type != Type.valueOf("ALL") && tier == Tier.valueOf("ALL")){
                pubgRepository.findAllByTypeOrderByIdDesc(pageable, type)
            } else pubgRepository.findAllByTypeAndTierOrderByIdDesc(pageable, type, tier)
        }
        else if(type == Type.valueOf("ALL")){
            boards = if(tier == Tier.valueOf("ALL")){
                pubgRepository.findAllByPlatformOrderByIdDesc(pageable, platform)
            } else  pubgRepository.findAllByPlatformAndTierOrderByIdDesc(pageable, platform, tier)
        }
        else{
            boards = if(tier == Tier.valueOf("ALL")){
                pubgRepository.findAllByPlatformAndTypeOrderByIdDesc(pageable, platform, type)
            } else  pubgRepository.findAllByPlatformAndTypeAndTierOrderByIdDesc(pageable, platform, type, tier)
        }
        // boards not found
        if(boards.isEmpty) throw BusinessException(ErrorCode.NO_BOARD_FOUND)
        result = PageResult.ok(boards.map { it.toReadPubgBoardDTO(playerRepository.findByIdAndTier(0, Tier.valueOf("DIAMOND")), getMemberList(it.id), getBanList(it.id))})

        for(i in 0 until boards.content.size){
            result.content[i].author = getPlayerByType().toPlayerResponseDTO()
        }
        return result
    }

    fun getPlayerByType(): Player {
        return Player(0, Tier.PLATINUM)
    }

    fun getMemberList(boardId: Long): List<String>{
        val board = pubgRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomId(board.get().chatRoomId)
        var memberList = mutableListOf<String>()
        for(element in chatRooms){
            if(element.oauth2Id == "banned")   continue
            element.nickname?.let { memberList.add(it) }
        }
        return memberList
    }

    fun getBanList(boardId: Long): List<String>{
        val board = pubgRepository.findById(boardId)
        val chatRooms = chatRepository.findAllByChatRoomIdAndOauth2Id(board.get().chatRoomId, "banned")
        var banList = mutableListOf<String>()
        for(element in chatRooms){
            element.nickname?.let { banList.add(it) }
        }
        return banList
    }

    @Transactional
    fun updateExpired(){
        val boards = pubgRepository.findAll()
        var expiredTime: LocalDateTime
        val now = LocalDateTime.now().plusHours(9)
        for(i in 0 until boards.size){

            if(boards[i].expired == "true")
                continue

            expiredTime = when(boards[i].expire){
                Expire.FIFTEEN_M -> boards[i].created.plusMinutes(15)
                Expire.THIRTY_M -> boards[i].created.plusMinutes(30)
                Expire.ONE_H -> boards[i].created.plusHours(1)
                Expire.TWO_H -> boards[i].created.plusHours(2)
                Expire.THREE_H -> boards[i].created.plusHours(3)
                Expire.SIX_H -> boards[i].created.plusHours(6)
                Expire.TWELVE_H -> boards[i].created.plusHours(12)
                Expire.TWENTY_FOUR_H -> boards[i].created.plusDays(1)
            }

            if(expiredTime.isBefore(now)) {
                boards[i].update("true")
            }
        }
    }
}