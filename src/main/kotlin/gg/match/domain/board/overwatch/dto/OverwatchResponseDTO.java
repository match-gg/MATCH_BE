package gg.match.domain.board.overwatch.dto;

import gg.match.controller.common.entity.Expire;

import java.util.Date;

public class OverwatchResponseDTO {

    private Long id;
    private String name;
    private String tier;
    private String position;
    private Boolean voice;
    private String content;
    private Expire expire;
    private Date regdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getVoice() {
        return voice;
    }

    public void setVoice(Boolean voice) {
        this.voice = voice;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Expire getExpire() {
        return expire;
    }

    public void setExpire(Expire expire) {
        this.expire = expire;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }
}
