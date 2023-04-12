package com.umjari.server.domain.groupqna.dto

import com.umjari.server.domain.groupqna.model.GroupQna
import com.umjari.server.domain.user.dto.UserDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class GroupQnaDto {
    data class CreateQnaRequest(
        @field:NotBlank val title: String?,
        @field:NotBlank val content: String?,
        @field:NotNull val isPrivate: Boolean?,
    )

    sealed class QnaDetailResponse {
        abstract val id: Long
        abstract val title: String
        abstract val content: String
        abstract val isPrivate: Boolean
    }

    data class PrivateQnaDetailResponse(
        override val id: Long,
        override val title: String,
        override val content: String,
        override val isPrivate: Boolean,
        val nickname: String,
    ) : QnaDetailResponse() {
        constructor(qna: GroupQna) : this(
            id = qna.id,
            title = qna.title,
            content = qna.content,
            isPrivate = qna.isPrivate,
            nickname = qna.authorNickname,
        )
    }

    data class NotPrivateQnaDetailResponse(
        override val id: Long,
        override val title: String,
        override val content: String,
        override val isPrivate: Boolean,
        val author: UserDto.SimpleUserDto,
    ) : QnaDetailResponse() {
        constructor(qna: GroupQna) : this(
            id = qna.id,
            title = qna.title,
            content = qna.content,
            isPrivate = qna.isPrivate,
            author = UserDto.SimpleUserDto(qna.author),
        )
    }

    sealed class QnaSimpleResponse {
        abstract val id: Long
        abstract val title: String
        abstract val isPrivate: Boolean
        abstract val replyCount: Int
    }

    data class PrivateQnaSimpleResponse(
        override val id: Long,
        override val title: String,
        override val isPrivate: Boolean,
        override val replyCount: Int,
        val nickname: String,
    ) : QnaSimpleResponse() {
        constructor(qna: SimpleQnaDto) : this(
            id = qna.id,
            title = qna.title,
            isPrivate = qna.private!!,
            nickname = qna.nickname,
            replyCount = qna.replyCount,
        )
    }

    data class NotPrivateQnaSimpleResponse(
        override val id: Long,
        override val title: String,
        override val isPrivate: Boolean,
        override val replyCount: Int,
        val author: UserDto.SimpleUserDto,
    ) : QnaSimpleResponse() {
        constructor(qna: SimpleQnaDto) : this(
            id = qna.id,
            title = qna.title,
            isPrivate = qna.private!!,
            author = UserDto.SimpleUserDto(qna.authorId, qna.authorNickname),
            replyCount = qna.replyCount,
        )
    }

    interface SimpleQnaDto {
        val id: Long
        val title: String
        val private: Boolean
        val nickname: String
        val authorId: Long
        val authorNickname: String
        val replyCount: Int
    }

    data class QnaDto(
        val qna: GroupQna,
        val replyCount: Long,
    )
}
