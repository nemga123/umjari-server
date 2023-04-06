package com.umjari.server.domain.group.service

import com.umjari.server.domain.group.exception.GroupRoleNotAuthorizedException
import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.repository.GroupMemberRepository
import org.springframework.stereotype.Service

@Service
class GroupMemberAuthorityService(
    private val groupMemberRepository: GroupMemberRepository,
) {
    private fun getMemberRole(groupId: Long, userId: Long): GroupMember.MemberRole {
        return groupMemberRepository.findByGroup_IdAndUser_Id(groupId, userId)?.role
            ?: GroupMember.MemberRole.NON_MEMBER
    }
    fun checkMemberAuthorities(expectedMemberRole: GroupMember.MemberRole, groupId: Long, userId: Long) {
        val memberRole = getMemberRole(groupId, userId)
        if (expectedMemberRole > memberRole) {
            throw GroupRoleNotAuthorizedException(expectedMemberRole)
        }
    }
}
