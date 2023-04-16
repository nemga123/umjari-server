package com.umjari.server.domain.groupqna.service

import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.service.GroupMemberAuthorityService
import com.umjari.server.domain.groupqna.dto.GroupQnaReplyDto
import com.umjari.server.domain.groupqna.exception.QnaIdNotFoundException
import com.umjari.server.domain.groupqna.model.GroupQnaReply
import com.umjari.server.domain.groupqna.repository.GroupQnaReplyRepository
import com.umjari.server.domain.groupqna.repository.GroupQnaRepository
import com.umjari.server.domain.user.model.User
import org.springframework.stereotype.Service

@Service
class GroupQnaReplyService(
    private val groupQnaReplyRepository: GroupQnaReplyRepository,
    private val groupQnaRepository: GroupQnaRepository,
    private val groupMemberAuthorityService: GroupMemberAuthorityService,
) {
    fun createReplyOnQna(
        groupId: Long,
        qnaId: Long,
        createReplyRequest: GroupQnaReplyDto.CreateReplyRequest,
        user: User,
    ) {
        val qna = groupQnaRepository.getByIdAndGroupId(qnaId, groupId)
            ?: throw QnaIdNotFoundException(groupId, qnaId)
        groupMemberAuthorityService.checkMemberAuthorities(GroupMember.MemberRole.MEMBER, groupId, user.id)

        val qnaReply = GroupQnaReply(
            author = user,
            qna = qna,
            content = createReplyRequest.content!!,
            isAnonymous = createReplyRequest.isAnonymous!!,
            authorNickname = user.nickname,
        )

        groupQnaReplyRepository.save(qnaReply)
    }
}
