package com.umjari.server.domain.group_qna.service

import com.umjari.server.domain.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.group_qna.dto.GroupQnaDto
import com.umjari.server.domain.group_qna.model.GroupQna
import com.umjari.server.domain.group_qna.repository.GroupQnaRepository
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class GroupQnaService(
    private val groupQnaRepository: GroupQnaRepository,
    private val groupRepository: GroupRepository,
) {
    fun createQna(createQnaRequest: GroupQnaDto.CreateQnaRequest, user: User, groupId: Long): GroupQnaDto.NotPrivateQnaResponse {
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
        return GroupQnaDto.NotPrivateQnaResponse(qna)
    }

    fun getQnaListByGroupId(groupId: Long, user: User?, pageable: Pageable): PageResponse<GroupQnaDto.QnaResponse> {
        if (!groupRepository.existsById(groupId)) {
            throw GroupIdNotFoundException(groupId)
        }

        val qnaList = groupQnaRepository.getAllByGroupId(groupId, pageable)
        val qnaResponses = qnaList.map {
            if (it.isPrivate && it.author.id != user?.id) {
                GroupQnaDto.PrivateQnaResponse(it)
            } else {
                GroupQnaDto.NotPrivateQnaResponse(it)
            }
        }
        return PageResponse(qnaResponses, pageable.pageNumber)
    }
}
