package gg.match.domain.board.lol.service

import gg.match.controller.common.dto.PageResult
import gg.match.domain.board.lol.repository.LoLRepository
import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.dto.ReadLoLBoardDTO
import gg.match.domain.board.lol.entity.LoL
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LoLService(
    private val loLRepository: LoLRepository
) {
    fun getBoards(pageable: Pageable): PageResult<ReadLoLBoardDTO> {
        val boards = loLRepository.findAll(pageable)
        return PageResult.ok(boards.map { it.toReadLoLBoardDTO() })
    }

    fun getBoard(boardId: Long): ReadLoLBoardDTO {
        val board = loLRepository.findById(boardId)
        return board.get().toReadLoLBoardDTO()
    }

    @Transactional
    fun save(loLRequestDTO: LoLRequestDTO): Long? {
        val board = loLRepository.save(loLRequestDTO.toEntity())
        return board.id
    }

    @Transactional
    fun update(boardId: Long, loLRequestDTO: LoLRequestDTO): ReadLoLBoardDTO {
        val board = loLRepository.findByIdOrNull(boardId)
            ?: throw Exception("not found")

        board.update(loLRequestDTO)
        return board.toReadLoLBoardDTO()
    }

    @Transactional
    fun delete(boardId: Long) {
        val board = loLRepository.findByIdOrNull(boardId)
            ?: throw Exception("not found")

        loLRepository.delete(board)
    }
}