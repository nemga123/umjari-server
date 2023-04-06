package com.umjari.server.domain.group.service

import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.repository.GroupMemberRepository
import org.springframework.stereotype.Service

@Service
class GroupMemberService(
    private val groupMemberRepository: GroupMemberRepository,
) {
    private fun getMemberRole(groupId: Long, userId: Long): GroupMember.MemberRole {
        return groupMemberRepository.findByGroup_IdAndUser_Id(groupId, userId)?.role
            ?: GroupMember.MemberRole.NON_MEMBER
    }

    fun checkMemberRole(expectedMemberRole: GroupMember.MemberRole, groupId: Long, userId: Long): Boolean {
        val memberRole = getMemberRole(groupId, userId)
        return expectedMemberRole <= memberRole
    }
}
