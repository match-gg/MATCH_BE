package gg.match.domain.Board.service;

import gg.match.domain.Board.dto.Board;
import gg.match.domain.Board.dto.BoardDto;
import gg.match.domain.Board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public BoardDto saveBoard(BoardDto boardDto) {
        Board board = new Board();
        board.setContent(boardDto.getContent());
        board.setNickname(boardDto.getNickname());
        board.setCreatedDateTime(LocalDateTime.now());
        board.setUpdatedDateTime(LocalDateTime.now());
        board = boardRepository.save(board);
        return new BoardDto(board);
    }

    @Override
    public List<BoardDto> getBoardList() {
        List<Board> boardList = boardRepository.findAll();
        return boardList.stream().map(BoardDto::new).collect(Collectors.toList());
    }

    @Override
    public BoardDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + id));
        return new BoardDto(board);
    }

    @Override
    public BoardDto updateBoard(Long id, BoardDto boardDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board Id:" + id));
        board.setContent(boardDto.getContent());
        board.setNickname(boardDto.getNickname());
        board.setUpdatedDateTime(LocalDateTime.now());
        board = boardRepository.save(board);
        return new BoardDto(board);
    }

    @Override
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    @Override
    public Long register(BoardDto boardDto) {
        return null;
    }
}


