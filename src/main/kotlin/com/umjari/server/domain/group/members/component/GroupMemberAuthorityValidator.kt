package com.umjari.server.domain.group.members.component

import com.umjari.server.domain.group.members.exception.GroupRoleNotAuthorizedException
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.group.members.repository.GroupMemberRepository
import com.umjari.server.domain.user.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class GroupMemberAuthorityValidator(
    private val groupMemberRepository: GroupMemberRepository,
    private val userRepository: UserRepository,
) {
    private fun getMemberRole(groupId: Long, userId: Long): GroupMember.MemberRole {
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId)?.role
            ?: GroupMember.MemberRole.NON_MEMBER
    }

    private fun isAdmin(userId: Long): Boolean {
        return userRepository.existsByIdAndRolesLike(userId, "%ROLE_ADMIN%")
    }
    fun checkMemberAuthorities(expectedMemberRole: GroupMember.MemberRole, groupId: Long, userId: Long) {
        if (isAdmin(userId)) {
            return
        }
        val memberRole = getMemberRole(groupId, userId)
        if (expectedMemberRole > memberRole) {
            throw GroupRoleNotAuthorizedException(expectedMemberRole)
        }
    }
}
