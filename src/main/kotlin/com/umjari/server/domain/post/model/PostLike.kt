package com.umjari.server.domain.post.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = [
                "user_id",
                "post_id",
            ],
        ),
    ],
)
class PostLike(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    val post: CommunityPost,
) : BaseEntity()
