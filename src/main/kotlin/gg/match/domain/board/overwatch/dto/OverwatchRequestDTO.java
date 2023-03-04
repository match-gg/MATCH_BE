package gg.match.domain.board.overwatch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gg.match.controller.common.entity.Expire;
import gg.match.domain.board.overwatch.entity.Position;
import gg.match.domain.board.overwatch.entity.Tier;
import gg.match.domain.board.overwatch.entity.Type;

import java.util.Date;

public class OverwatchRequestDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("tier")
    private String tier;

    @JsonProperty("position")
    private String position;

    @JsonProperty("voice")
    private Boolean voice;

    @JsonProperty("content")
    private String content;

    @JsonProperty("expire")
    private String expire;

    @JsonProperty("regdate")
    private Date regdate;

    // Getter/Setter 메서드
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

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }
}
