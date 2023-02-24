package gg.match.domain.board.example.repository;

import gg.match.domain.board.example.dto.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}

