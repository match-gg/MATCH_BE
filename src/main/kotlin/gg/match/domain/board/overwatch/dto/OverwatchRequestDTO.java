package gg.match.domain.board.overwatch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gg.match.controller.common.entity.Expire;
import gg.match.domain.board.overwatch.entity.Position;
import gg.match.domain.board.overwatch.entity.Tier;
import gg.match.domain.board.overwatch.entity.Type;
public class OverwatchRequestDTO {
    @JsonProperty("content")
    private String content;

    @JsonProperty("tier")
    private String tier;
}
