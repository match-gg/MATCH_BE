package gg.match.domain.board.overwatch.entity;

import gg.match.controller.common.entity.Expire;
import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "overwatch")
public class Overwatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Tier tier;
    @Enumerated(EnumType.STRING)
    private Position position;
    private Boolean voice;
    private String content;
    @Enumerated(EnumType.STRING)
    private Expire expire;
    private Date regdate;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setVoice(Boolean voice) {
        this.voice = voice;
    }

    public Boolean getVoice() {
        return voice;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setExpire(Expire expire) {
        this.expire = expire;
    }

    public Expire getExpire() {
        return expire;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public Date getRegdate() {
        return regdate;
    }
}
