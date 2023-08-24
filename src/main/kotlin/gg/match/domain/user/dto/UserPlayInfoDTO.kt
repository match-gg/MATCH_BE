package gg.match.domain.user.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UserPlayInfoDTO (
    @JsonProperty("matchCount")
    var matchCount: Long,

    @JsonProperty("likeCount")
    var likeCount: Long,

    @JsonProperty("dislikeCount")
    var dislikeCount: Long
)