package gg.match.domain.board.example.repository;

import gg.match.domain.board.example.dto.Board;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board save(Board board) {
        return boardRepository.save(board);
    }

    public Optional<Board> findById(Long id) {
        return boardRepository.findById(id);
    }

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    // 추가로 필요한 메서드가 있다면 구현
}
