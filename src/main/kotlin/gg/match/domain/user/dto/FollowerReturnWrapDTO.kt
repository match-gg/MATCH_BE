package gg.match.domain.user.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class FollowerReturnWrapDTO (
    @JsonProperty("followers")
    var followers: List<FollowerReturnDTO>
)