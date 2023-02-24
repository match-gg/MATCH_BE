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
    private String tier;
    private String position;
    private Boolean voice;
    private String content;
    private String expire;
    private Date regdate;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
