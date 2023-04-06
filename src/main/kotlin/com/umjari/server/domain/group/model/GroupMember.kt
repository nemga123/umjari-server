package com.umjari.server.domain.group.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["group_id", "user_id"]),
    ],
)
class GroupMember(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    var group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: User,

    @Enumerated(EnumType.ORDINAL)
    val role: MemberRole = MemberRole.MEMBER,
) : BaseEntity() {
    enum class MemberRole {
        NON_MEMBER, MEMBER, ADMIN,
    }
}
