package gg.match.common.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created", updatable = false)
    var created: LocalDateTime = LocalDateTime.now().plusHours(9)

    @LastModifiedDate
    @Column(name = "updated")
    var updated: LocalDateTime = LocalDateTime.now().plusHours(9)
}