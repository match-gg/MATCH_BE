package gg.match.domain.board.dto;

public class BoardMapper {
    public static BoardDto toDto(Board board) {
        BoardDto boardDto = new BoardDto();
        boardDto.setId(board.getId());
        boardDto.setContent(board.getContent());
        boardDto.setNickname(board.getNickname());
        boardDto.setCreatedDateTime(board.getCreatedDateTime());
        boardDto.setUpdatedDateTime(board.getUpdatedDateTime());
        return boardDto;
    }

    public static Board toEntity(BoardDto boardDto) {
        Board board = new Board();
        board.setId(boardDto.getId());
        board.setContent(boardDto.getContent());
        board.setNickname(boardDto.getNickname());
        board.setCreatedDateTime(boardDto.getCreatedDateTime());
        board.setUpdatedDateTime(boardDto.getUpdatedDateTime());
        return board;
    }
}
