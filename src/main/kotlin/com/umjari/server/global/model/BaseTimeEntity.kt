package com.umjari.server.global.model

import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseTimeEntity: BaseEntity() {
    @CreatedDate
    open var createdAt: LocalDateTime? = null

    @LastModifiedDate
    open var updatedAt: LocalDateTime? = null
}