package com.umjari.server.domain.groupqna.dto

import com.umjari.server.domain.groupqna.model.GroupQnaReply
import com.umjari.server.domain.user.dto.UserDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class GroupQnaReplyDto {
    data class CreateReplyRequest(
        @field:NotBlank val content: String?,
        @field:NotNull val isAnonymous: Boolean?,
    )

    sealed class QnaReplyResponse {
        abstract val id: Long
        abstract val content: String
        abstract val createAt: String
        abstract val updatedAt: String
        abstract val isAnonymous: Boolean
        abstract val isAuthor: Boolean
    }

    data class AnonymousQnaReplyResponse(
        override val id: Long,
        override val content: String,
        override val createAt: String,
        override val updatedAt: String,
        override val isAnonymous: Boolean,
        val nickname: String,
        override val isAuthor: Boolean,
    ) : QnaReplyResponse() {
        constructor(qnaReply: GroupQnaReply, isAuthor: Boolean) : this(
            id = qnaReply.id,
            content = qnaReply.content,
            createAt = qnaReply.createdAt!!.toString(),
            updatedAt = qnaReply.updatedAt!!.toString(),
            isAnonymous = qnaReply.isAnonymous,
            nickname = qnaReply.authorNickname,
            isAuthor = isAuthor,
        )
    }

    data class NotAnonymousQnaReplyResponse(
        override val id: Long,
        override val content: String,
        override val createAt: String,
        override val updatedAt: String,
        override val isAnonymous: Boolean,
        val author: UserDto.SimpleUserDto,
        override val isAuthor: Boolean,
    ) : QnaReplyResponse() {
        constructor(qnaReply: GroupQnaReply, isAuthor: Boolean) : this(
            id = qnaReply.id,
            content = qnaReply.content,
            createAt = qnaReply.createdAt!!.toString(),
            updatedAt = qnaReply.updatedAt!!.toString(),
            isAnonymous = qnaReply.isAnonymous,
            author = UserDto.SimpleUserDto(qnaReply.author),
            isAuthor = isAuthor,
        )
    }
}
