package com.umjari.server.domain.groupqna.service

import com.umjari.server.domain.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.groupqna.dto.GroupQnaDto
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
    ): GroupQnaDto.NotPrivateQnaDetailResponse {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        val qna = GroupQna(
            author = user,
            authorNickname = user.nickname,
            group = group,
            title = createQnaRequest.title!!,
            content = createQnaRequest.content!!,
            isPrivate = createQnaRequest.isPrivate!!,
        )
        groupQnaRepository.save(qna)
        return GroupQnaDto.NotPrivateQnaDetailResponse(qna)
    }

    fun getQnaListByGroupId(
        groupId: Long,
        user: User?,
        pageable: Pageable,
    ): PageResponse<GroupQnaDto.QnaSimpleResponse> {
        if (!groupRepository.existsById(groupId)) {
            throw GroupIdNotFoundException(groupId)
        }

        val qnaList = groupQnaRepository.getSimpleResponseByGroupId(groupId, pageable)
        print(qnaList.toList())
        val qnaResponses = qnaList.map {
            if (it.private!! && it.authorId != user?.id) {
                GroupQnaDto.PrivateQnaSimpleResponse(it)
            } else {
                GroupQnaDto.NotPrivateQnaSimpleResponse(it)
            }
        }
        return PageResponse(qnaResponses, pageable.pageNumber)
    }

    fun getQna(groupId: Long, qnaId: Long, user: User?): GroupQnaDto.QnaDetailResponse {
        val qna = groupQnaRepository.getByIdAndGroupId(qnaId, groupId)
            ?: throw QnaIdNotFountException(groupId, qnaId)
        return if (qna.isPrivate && qna.author.id != user?.id) {
            GroupQnaDto.PrivateQnaDetailResponse(qna)
        } else {
            GroupQnaDto.NotPrivateQnaDetailResponse(qna)
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
            isPrivate = updateGroupQnaRequest.isPrivate!!
        }
        groupQnaRepository.save(qna)
    }
}
