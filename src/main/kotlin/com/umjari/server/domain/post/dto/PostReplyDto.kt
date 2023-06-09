package com.umjari.server.domain.post.dto

import com.umjari.server.domain.post.model.CommunityPostReply
import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class PostReplyDto {
    data class CreateReplyRequest(
        @field:NotBlank val content: String?,
        @field:NotNull val isAnonymous: Boolean,
    )

    sealed class PostReplyResponse {
        abstract val id: Long
        abstract val content: String
        abstract val createAt: String
        abstract val updatedAt: String
        abstract val isAnonymous: Boolean
        abstract val isAuthor: Boolean
        abstract val isDeleted: Boolean
    }

    data class AnonymousPostReplyResponse(
        override val id: Long,
        override val content: String,
        override val createAt: String,
        override val updatedAt: String,
        override val isAnonymous: Boolean,
        val nickname: String,
        override val isAuthor: Boolean,
        override val isDeleted: Boolean,
    ) : PostReplyResponse() {
        constructor(reply: CommunityPostReply, user: User?) : this(
            id = reply.id,
            content = if (reply.isDeleted) "삭제된 댓글입니다." else reply.content,
            createAt = reply.createdAt!!.toString(),
            updatedAt = reply.updatedAt!!.toString(),
            isAnonymous = reply.isAnonymous,
            nickname = reply.authorNickname,
            isAuthor = reply.author.id == user?.id,
            isDeleted = reply.isDeleted,
        )
    }

    data class NotAnonymousPostReplyResponse(
        override val id: Long,
        override val content: String,
        override val createAt: String,
        override val updatedAt: String,
        override val isAnonymous: Boolean,
        val authorInfo: UserDto.SimpleUserDto,
        override val isAuthor: Boolean,
        override val isDeleted: Boolean,
    ) : PostReplyResponse() {
        constructor(reply: CommunityPostReply, user: User?) : this(
            id = reply.id,
            content = if (reply.isDeleted) "삭제된 댓글입니다." else reply.content,
            createAt = reply.createdAt!!.toString(),
            updatedAt = reply.updatedAt!!.toString(),
            isAnonymous = reply.isAnonymous,
            authorInfo = UserDto.SimpleUserDto(reply.author),
            isAuthor = reply.author.id == user?.id,
            isDeleted = reply.isDeleted,
        )
    }
}
