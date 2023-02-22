package gg.match.domain.Board.service;

import gg.match.domain.Board.dto.BoardDto;

import java.util.List;

public interface BoardService {
    BoardDto saveBoard(BoardDto boardDto);
    List<BoardDto> getBoardList();
    BoardDto getBoard(Long id);
    BoardDto updateBoard(Long id, BoardDto boardDto);
    void deleteBoard(Long id);

    Long register(BoardDto boardDto);
}
