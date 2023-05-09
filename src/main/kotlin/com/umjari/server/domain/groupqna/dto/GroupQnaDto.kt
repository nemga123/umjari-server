package com.umjari.server.domain.groupqna.dto

import com.umjari.server.domain.groupqna.model.GroupQna
import com.umjari.server.domain.user.dto.UserDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

class GroupQnaDto {
    data class CreateQnaRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val title: String?,
        @field:NotBlank val content: String?,
        @field:NotNull val isAnonymous: Boolean?,
    )

    sealed class QnaDetailResponse {
        abstract val id: Long
        abstract val title: String
        abstract val content: String
        abstract val isAnonymous: Boolean
        abstract val replyList: List<GroupQnaReplyDto.QnaReplyResponse>
    }

    data class AnonymousQnaDetailResponse(
        override val id: Long,
        override val title: String,
        override val content: String,
        override val isAnonymous: Boolean,
        override val replyList: List<GroupQnaReplyDto.QnaReplyResponse>,
        val nickname: String,
    ) : QnaDetailResponse() {
        constructor(qna: GroupQna, replyList: List<GroupQnaReplyDto.QnaReplyResponse>) : this(
            id = qna.id,
            title = qna.title,
            content = qna.content,
            isAnonymous = true,
            nickname = qna.authorNickname,
            replyList = replyList,
        )
    }

    data class NotAnonymousQnaDetailResponse(
        override val id: Long,
        override val title: String,
        override val content: String,
        override val replyList: List<GroupQnaReplyDto.QnaReplyResponse>,
        override val isAnonymous: Boolean,
        val author: UserDto.SimpleUserDto,
    ) : QnaDetailResponse() {
        constructor(qna: GroupQna, replyList: List<GroupQnaReplyDto.QnaReplyResponse>) : this(
            id = qna.id,
            title = qna.title,
            content = qna.content,
            isAnonymous = false,
            author = UserDto.SimpleUserDto(qna.author),
            replyList = replyList,
        )

        constructor(qna: GroupQna) : this(
            id = qna.id,
            title = qna.title,
            content = qna.content,
            isAnonymous = qna.isAnonymous,
            author = UserDto.SimpleUserDto(qna.author),
            replyList = arrayListOf(),
        )
    }

    sealed class QnaSimpleResponse {
        abstract val id: Long
        abstract val title: String
        abstract val isAnonymous: Boolean
        abstract val replyCount: Int
        abstract val createAt: String
        abstract val updatedAt: String
    }

    data class AnonymousQnaSimpleResponse(
        override val id: Long,
        override val title: String,
        override val isAnonymous: Boolean,
        override val replyCount: Int,
        val nickname: String,
        override val createAt: String,
        override val updatedAt: String,
    ) : QnaSimpleResponse() {
        constructor(qna: SimpleQnaDto) : this(
            id = qna.id,
            title = qna.title,
            isAnonymous = qna.anonymous,
            nickname = qna.nickname,
            replyCount = qna.replyCount,
            createAt = qna.createAt!!.toString(),
            updatedAt = qna.updatedAt!!.toString(),
        )
    }

    data class NotAnonymousQnaSimpleResponse(
        override val id: Long,
        override val title: String,
        override val isAnonymous: Boolean,
        override val replyCount: Int,
        val author: UserDto.SimpleUserDto,
        override val createAt: String,
        override val updatedAt: String,
    ) : QnaSimpleResponse() {
        constructor(qna: SimpleQnaDto) : this(
            id = qna.id,
            title = qna.title,
            isAnonymous = qna.anonymous,
            author = UserDto.SimpleUserDto(qna.authorId, qna.authorNickname),
            replyCount = qna.replyCount,
            createAt = qna.createAt!!.toString(),
            updatedAt = qna.updatedAt!!.toString(),
        )
    }

    interface SimpleQnaDto {
        val id: Long
        val title: String
        val anonymous: Boolean
        val nickname: String
        val authorId: Long
        val authorNickname: String
        val replyCount: Int
        val createAt: LocalDateTime?
        val updatedAt: LocalDateTime?
    }
}
