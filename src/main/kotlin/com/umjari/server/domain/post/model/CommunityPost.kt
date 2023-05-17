package com.umjari.server.domain.post.model

import com.umjari.server.domain.group.model.Instrument
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "community_post")
class CommunityPost(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    val author: User,

    @field:NotNull
    var board: Instrument,

    @field:NotBlank
    var title: String,

    @field:NotBlank
    @Column(columnDefinition = "TEXT")
    var content: String,

    @field:NotNull
    var isAnonymous: Boolean,

    @field:NotBlank
    val authorNickname: String,
) : BaseTimeEntity()
