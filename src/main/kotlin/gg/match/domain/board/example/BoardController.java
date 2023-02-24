package gg.match.domain.board.example;

import gg.match.domain.board.example.dto.Board;
import gg.match.domain.board.example.repository.BoardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api/board")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/save")
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

