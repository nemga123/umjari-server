package com.umjari.server.domain.friend.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotNull

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = [
                "requester_id",
                "receiver_id",
            ],
        ),
    ],
)
class Friend(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    val requester: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    val receiver: User,

    @field:NotNull
    var status: FriendshipStatus,
) : BaseTimeEntity() {
    enum class FriendshipStatus {
        FENDING, APPROVED
    }
}
