package gg.match.domain.user.repository

import gg.match.domain.user.entity.Follow
import org.springframework.data.jpa.repository.JpaRepository

interface FollowRepository: JpaRepository<Follow, Long> {
    fun findAllByOauth2Id(oauth2Id: String): List<Follow>
}