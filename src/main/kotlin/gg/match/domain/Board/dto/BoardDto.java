package gg.match.domain.Board.dto;

import gg.match.domain.Board.dto.Board;

import java.time.LocalDateTime;

public class BoardDto {
    private Long id;
    private String content;
    private String nickname;
    private LocalDateTime createdDateTime;
    private LocalDateTime updatedDateTime;

    // 생성자
    public BoardDto() {
    }

    public BoardDto(Board board) {
        this.id = board.getId();
        this.content = board.getContent();
        this.nickname = board.getNickname();
        this.createdDateTime = board.getCreatedDateTime();
        this.updatedDateTime = board.getUpdatedDateTime();
    }

    // Getter/Setter 메서드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}