package com.umjari.server.domain.groupqna.model

import com.umjari.server.domain.group.group.model.Group
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "umjari_group_qna")
class GroupQna(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    val author: User,

    @field:NotBlank
    val authorNickname: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    val group: Group,

    @field:NotBlank
    var title: String,

    @field:NotBlank
    @Column(columnDefinition = "TEXT")
    var content: String,

    @field:NotNull
    var isAnonymous: Boolean,

    @OneToMany(mappedBy = "qna", cascade = [CascadeType.REMOVE])
    var replies: MutableList<GroupQnaReply> = mutableListOf(),
) : BaseTimeEntity()
