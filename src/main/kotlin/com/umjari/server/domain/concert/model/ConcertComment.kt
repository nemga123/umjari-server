package com.umjari.server.domain.concert.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
class ConcertComment(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", referencedColumnName = "id")
    val concert: Concert,

    @field:NotNull
    @field:Size(max = 500)
    var comment: String,
) : BaseTimeEntity()
