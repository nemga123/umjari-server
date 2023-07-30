package com.umjari.server.domain.groupqna.service

import com.umjari.server.domain.group.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.group.repository.GroupRepository
import com.umjari.server.domain.groupqna.dto.GroupQnaDto
import com.umjari.server.domain.groupqna.dto.GroupQnaReplyDto
import com.umjari.server.domain.groupqna.exception.QnaCannotBeDeletedException
import com.umjari.server.domain.groupqna.exception.QnaCannotBeUpdatedException
import com.umjari.server.domain.groupqna.exception.QnaIdNotFoundException
import com.umjari.server.domain.groupqna.model.GroupQna
import com.umjari.server.domain.groupqna.repository.GroupQnaReplyRepository
import com.umjari.server.domain.groupqna.repository.GroupQnaRepository
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
            isAnonymous = createQnaRequest.isAnonymous!!,
        )
        groupQnaRepository.save(qna)
        return GroupQnaDto.NotAnonymousQnaDetailResponse(qna, true)
    }

    fun getQnaListByGroupId(
        groupId: Long,
        searchText: String,
        pageable: Pageable,
    ): PageResponse<GroupQnaDto.QnaSimpleResponse> {
        if (!groupRepository.existsById(groupId)) {
            throw GroupIdNotFoundException(groupId)
        }

        val qnaList = if (searchText == "") {
            groupQnaRepository.getSimpleResponseByGroupIdWithReplyCounts(groupId, pageable)
        } else {
            groupQnaRepository.getSimpleResponseByGroupIdAndSearchTextWithReplyCounts(groupId, searchText, pageable)
        }

        val qnaResponses = qnaList.map {
            if (it.anonymous) {
                GroupQnaDto.AnonymousQnaSimpleResponse(it)
            } else {
                GroupQnaDto.NotAnonymousQnaSimpleResponse(it)
            }
        }
        return PageResponse(qnaResponses, pageable.pageNumber)
    }

    fun getQna(groupId: Long, qnaId: Long, user: User?): GroupQnaDto.QnaDetailResponse {
        val qna = groupQnaRepository.getByIdAndGroupId(qnaId, groupId)
            ?: throw QnaIdNotFoundException(groupId, qnaId)
        val replyList = groupQnaReplyRepository.getAllByQnaIdWithUser(qna.id)
        val replyResponseList = replyList.map {
            if (it.isAnonymous) {
                GroupQnaReplyDto.AnonymousQnaReplyResponse(it, it.author.id == user?.id)
            } else {
                GroupQnaReplyDto.NotAnonymousQnaReplyResponse(it, it.author.id == user?.id)
            }
        }
        return if (qna.isAnonymous) {
            GroupQnaDto.AnonymousQnaDetailResponse(qna, replyResponseList, qna.author.id == user?.id)
        } else {
            GroupQnaDto.NotAnonymousQnaDetailResponse(qna, replyResponseList, qna.author.id == user?.id)
        }
    }

    fun updateQna(groupId: Long, qnaId: Long, user: User, updateGroupQnaRequest: GroupQnaDto.CreateQnaRequest) {
        val qna = groupQnaRepository.getByIdAndGroupId(qnaId, groupId)
            ?: throw QnaIdNotFoundException(groupId, qnaId)
        if (qna.author.id != user.id) throw QnaIdNotFoundException(groupId, qnaId)
        if (groupQnaReplyRepository.existsByQnaId(qna.id)) throw QnaCannotBeUpdatedException(qnaId)
        with(qna) {
            title = updateGroupQnaRequest.title!!
            content = updateGroupQnaRequest.content!!
            isAnonymous = updateGroupQnaRequest.isAnonymous!!
        }
        groupQnaRepository.save(qna)
    }

    @Transactional
    fun deleteQna(groupId: Long, qnaId: Long, user: User) {
        val qna = groupQnaRepository.getByIdAndGroupId(qnaId, groupId)
            ?: throw QnaIdNotFoundException(groupId, qnaId)
        if (qna.author.id != user.id) throw QnaIdNotFoundException(groupId, qnaId)
        if (groupQnaReplyRepository.existsByQnaId(qna.id)) throw QnaCannotBeDeletedException(qnaId)
        groupQnaRepository.delete(qna)
    }
}
