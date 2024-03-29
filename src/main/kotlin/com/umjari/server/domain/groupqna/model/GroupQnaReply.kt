package com.umjari.server.domain.groupqna.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
class GroupQnaReply(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    val author: User,

    @field:NotNull
    val authorNickname: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_id", referencedColumnName = "id")
    val qna: GroupQna,

    @field:NotBlank
    @Column(columnDefinition = "TEXT")
    var content: String,

    @field:NotNull
    var isAnonymous: Boolean,
) : BaseTimeEntity()
