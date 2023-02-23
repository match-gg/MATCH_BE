package gg.match.domain.board.lol.service

import gg.match.domain.board.lol.dto.LoLRequestDTO
import gg.match.domain.board.lol.repository.LoLRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LoLService(
    private val loLRepository: LoLRepository
) {
    fun saveContent(loLRequestDTO: LoLRequestDTO): String{
        return loLRequestDTO.content
    }
}