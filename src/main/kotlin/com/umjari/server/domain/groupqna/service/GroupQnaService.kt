package com.umjari.server.domain.groupqna.service

import com.umjari.server.domain.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.groupqna.dto.GroupQnaDto
import com.umjari.server.domain.groupqna.dto.GroupQnaReplyDto
import com.umjari.server.domain.groupqna.exception.QnaCannotBeUpdatedException
import com.umjari.server.domain.groupqna.exception.QnaIdNotFountException
import com.umjari.server.domain.groupqna.model.GroupQna
import com.umjari.server.domain.groupqna.repository.GroupQnaReplyRepository
import com.umjari.server.domain.groupqna.repository.GroupQnaRepository
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GroupQnaService(
    private val groupQnaRepository: GroupQnaRepository,
    private val groupRepository: GroupRepository,
    private val groupQnaReplyRepository: GroupQnaReplyRepository,
) {
    fun createQna(
        createQnaRequest: GroupQnaDto.CreateQnaRequest,
        user: User,
        groupId: Long,
    ): GroupQnaDto.NotAnonymousQnaDetailResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        val qna = GroupQna(
            author = user,
            authorNickname = user.nickname,
            group = group,
            title = createQnaRequest.title!!,
            content = createQnaRequest.content!!,
            isAnonymous = createQnaRequest.isPrivate!!,
        )
        groupQnaRepository.save(qna)
        return GroupQnaDto.NotAnonymousQnaDetailResponse(qna)
    }

    fun getQnaListByGroupId(
        groupId: Long,
        user: User?,
        pageable: Pageable,
    ): PageResponse<GroupQnaDto.QnaSimpleResponse> {
        if (!groupRepository.existsById(groupId)) {
            throw GroupIdNotFoundException(groupId)
        }

        val qnaList = groupQnaRepository.getSimpleResponseByGroupIdWithReplyCounts(groupId, pageable)

        val qnaResponses = qnaList.map {
            if (it.anonymous && it.authorId != user?.id) {
                GroupQnaDto.AnonymousQnaSimpleResponse(it)
            } else {
                GroupQnaDto.NotAnonymousQnaSimpleResponse(it)
            }
        }
        return PageResponse(qnaResponses, pageable.pageNumber)
    }

    fun getQna(groupId: Long, qnaId: Long, user: User?): GroupQnaDto.QnaDetailResponse {
        val qna = groupQnaRepository.getByIdAndGroupId(qnaId, groupId)
            ?: throw QnaIdNotFountException(groupId, qnaId)
        val replyList = groupQnaReplyRepository.getAllByQnaIdWithUser(qna.id)
        val replyResponseList = replyList.map {
            if (it.isAnonymous && it.author.id != user?.id) {
                GroupQnaReplyDto.AnonymousQnaReplyResponse(it)
            } else {
                GroupQnaReplyDto.NotAnonymousQnaReplyResponse(it)
            }
        }
        return if (qna.isAnonymous && qna.author.id != user?.id) {
            GroupQnaDto.AnonymousQnaDetailResponse(qna, replyResponseList)
        } else {
            GroupQnaDto.NotAnonymousQnaDetailResponse(qna, replyResponseList)
        }
    }

    fun updateQna(groupId: Long, qnaId: Long, user: User, updateGroupQnaRequest: GroupQnaDto.CreateQnaRequest) {
        val qna = groupQnaRepository.getByIdAndGroupId(qnaId, groupId)
            ?: throw QnaIdNotFountException(groupId, qnaId)
        if (qna.author.id != user.id) throw QnaIdNotFountException(groupId, qnaId)
        if (!groupQnaReplyRepository.existsByQnaId(qna.id)) throw QnaCannotBeUpdatedException(qnaId)
        with(qna) {
            title = updateGroupQnaRequest.title!!
            content = updateGroupQnaRequest.content!!
            isAnonymous = updateGroupQnaRequest.isPrivate!!
        }
        groupQnaRepository.save(qna)
    }
}
