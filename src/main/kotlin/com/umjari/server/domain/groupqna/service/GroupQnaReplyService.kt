package com.umjari.server.domain.groupqna.service

import com.umjari.server.domain.group.members.component.GroupMemberAuthorityValidator
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.groupqna.dto.GroupQnaReplyDto
import com.umjari.server.domain.groupqna.exception.QnaIdNotFoundException
import com.umjari.server.domain.groupqna.exception.QnaReplyIdNotFoundException
import com.umjari.server.domain.groupqna.model.GroupQnaReply
import com.umjari.server.domain.groupqna.repository.GroupQnaReplyRepository
import com.umjari.server.domain.groupqna.repository.GroupQnaRepository
import com.umjari.server.domain.user.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupQnaReplyService(
    private val groupQnaReplyRepository: GroupQnaReplyRepository,
    private val groupQnaRepository: GroupQnaRepository,
    private val groupMemberAuthorityValidator: GroupMemberAuthorityValidator,
) {
    fun createReplyOnQna(
        groupId: Long,
        qnaId: Long,
        createReplyRequest: GroupQnaReplyDto.CreateReplyRequest,
        user: User,
    ) {
        val qna = groupQnaRepository.getByIdAndGroupId(qnaId, groupId)
            ?: throw QnaIdNotFoundException(groupId, qnaId)
        groupMemberAuthorityValidator.checkMemberAuthorities(GroupMember.MemberRole.MEMBER, groupId, user.id)

        GroupQnaReply(
            author = user,
            qna = qna,
            content = createReplyRequest.content!!,
            isAnonymous = createReplyRequest.isAnonymous!!,
            authorNickname = user.nickname,
        ).also { groupQnaReply -> groupQnaReplyRepository.save(groupQnaReply) }
    }

    fun updateReply(
        groupId: Long,
        qnaId: Long,
        replyId: Long,
        updateReplyRequest: GroupQnaReplyDto.CreateReplyRequest,
        user: User,
    ) {
        val reply = groupQnaReplyRepository.findByIdAndQnaIdAndQnaGroupId(replyId, qnaId, groupId)
            ?: throw QnaReplyIdNotFoundException(replyId)

        if (reply.author.id != user.id) throw QnaReplyIdNotFoundException(replyId)

        with(reply) {
            content = updateReplyRequest.content!!
            isAnonymous = updateReplyRequest.isAnonymous!!
        }

        groupQnaReplyRepository.save(reply)
    }

    @Transactional
    fun deleteReply(
        groupId: Long,
        qnaId: Long,
        replyId: Long,
        user: User,
    ) {
        val reply = groupQnaReplyRepository.findByIdAndQnaIdAndQnaGroupId(replyId, qnaId, groupId)
            ?: throw QnaReplyIdNotFoundException(replyId)

        if (reply.author.id != user.id) throw QnaReplyIdNotFoundException(replyId)

        groupQnaReplyRepository.delete(reply)
    }
}
