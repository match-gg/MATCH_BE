package gg.match.domain.user.repository

import gg.match.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByOauth2Id(oauth2Id: String): User?
    fun existsByOauth2Id(oauth2Id: String): Boolean
    fun findByLol(lol: String): User?
    fun findByPubg(pubg: String): User?
    fun findByOverwatch(overwatch: String): User?
}