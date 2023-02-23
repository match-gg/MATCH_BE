package gg.match.domain.board.dto;

import gg.match.controller.common.entity.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //figma에 140자 제한이라 140자로 제한 / 일단하지마
    //@Column(length = 140, nullable = false)Z
    private String content;

    private String nickname;

    //생성시간
    private LocalDateTime createdDateTime;

    //최근갱신시간, 카드를 상단으로 끌어올릴때 필요
    private LocalDateTime updatedDateTime;

    // Getter/Setter 메서드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedDateTime(){ return createdDateTime; }
    public void setCreatedDateTime(LocalDateTime updatedDateTime){
        this.createdDateTime = createdDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime createdDateTime){
        this.updatedDateTime = updatedDateTime; }


    public LocalDateTime getUpdatedDateTime(){
        return updatedDateTime;
    }
}