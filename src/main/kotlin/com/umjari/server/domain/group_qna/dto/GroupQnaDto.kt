package com.umjari.server.domain.group_qna.dto

import com.umjari.server.domain.group_qna.model.GroupQna
import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class GroupQnaDto {
    data class CreateQnaRequest(
        @field:NotBlank val title: String?,
        @field:NotBlank val content: String?,
        @field:NotNull val isPrivate: Boolean?,
    )

    sealed class QnaResponse{
        abstract val id: Long
        abstract val title: String
        abstract val content: String
        abstract val isPrivate: Boolean
    }

    data class PrivateQnaResponse(
        override val id: Long,
        override val title: String,
        override val content: String,
        override val isPrivate: Boolean,
        val nickname: String,
    ): QnaResponse() {
        constructor(qna: GroupQna) : this(
            id = qna.id,
            title = qna.title,
            content = qna.content,
            isPrivate = qna.isPrivate,
            nickname = qna.authorNickname,
        )
    }

    data class NotPrivateQnaResponse(
        override val id: Long,
        override val title: String,
        override val content: String,
        override val isPrivate: Boolean,
        val author: UserDto.SimpleUserDto,
    ): QnaResponse() {
        constructor(qna: GroupQna) : this(
            id = qna.id,
            title = qna.title,
            content = qna.content,
            isPrivate = qna.isPrivate,
            author = UserDto.SimpleUserDto(qna.author),
        )
    }
}
