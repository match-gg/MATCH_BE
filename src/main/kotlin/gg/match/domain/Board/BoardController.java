package gg.match.domain.Board;

import gg.match.domain.Board.dto.Board;
import gg.match.domain.Board.repository.BoardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/boards")
    public Board save(@RequestBody Board board) {
        return boardService.save(board);
    }

    @GetMapping("/boards/{id}")
    public Board findById(@PathVariable Long id) {
        return boardService.findById(id).orElse(null);
    }

    @GetMapping("/boards")
    public List<Board> findAll() {
        return boardService.findAll();
    }

    @GetMapping("/test")
    public String test(){
        return "test";
    }
    // 추가로 필요한 메서드가 있다면 구현
}

