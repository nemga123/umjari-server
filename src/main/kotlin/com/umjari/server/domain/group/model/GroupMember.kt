package com.umjari.server.domain.group.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.Date

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
    var role: MemberRole = MemberRole.MEMBER,

    var joinedAt: Date? = null,

    var leavedAt: Date? = null,
) : BaseEntity() {
    enum class MemberRole {
        NON_MEMBER, MEMBER, ADMIN,
    }
}
