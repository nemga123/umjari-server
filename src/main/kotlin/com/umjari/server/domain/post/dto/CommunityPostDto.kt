package com.umjari.server.domain.post.dto

import com.umjari.server.domain.group.model.Instrument
import com.umjari.server.domain.post.model.CommunityPost
import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class CommunityPostDto {
    data class CreateCommunityPostRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val title: String?,
        @field:NotBlank
        val content: String?,
        @field:NotNull
        val isAnonymous: Boolean,
    )

    data class UpdateCommunityPostRequest(
        @field:NotNull
        val board: Instrument,
        @field:NotBlank
        @field:Size(max = 255)
        val title: String?,
        @field:NotBlank
        val content: String?,
        @field:NotNull
        val isAnonymous: Boolean,
    )

    sealed class PostDetailResponse {
        abstract val id: Long
        abstract val board: String
        abstract val title: String
        abstract val content: String
        abstract val isAnonymous: Boolean
        abstract val createAt: String
        abstract val updatedAt: String
        abstract val isAuthor: Boolean
        abstract val replies: List<PostReplyDto.PostReplyResponse>
    }

    data class AnonymousPostDetailResponse(
        override val id: Long,
        override val board: String,
        override val title: String,
        override val content: String,
        override val isAnonymous: Boolean,
        override val createAt: String,
        override val updatedAt: String,
        override val isAuthor: Boolean,
        val nickname: String,
        override val replies: List<PostReplyDto.PostReplyResponse>,
    ) : PostDetailResponse() {
        constructor(post: CommunityPost, user: User) : this(
            id = post.id,
            board = post.board.instrumentName,
            title = post.title,
            content = post.content,
            isAnonymous = post.isAnonymous,
            createAt = post.createdAt.toString(),
            updatedAt = post.updatedAt.toString(),
            isAuthor = post.author.id == user.id,
            nickname = post.authorNickname,
            replies = post.replies.map {
                if (it.isAnonymous) {
                    PostReplyDto.AnonymousPostReplyResponse(it, user)
                } else {
                    PostReplyDto.NotAnonymousPostReplyResponse(
                        it,
                        user,
                    )
                }
            },
        )

        constructor(post: CommunityPost, user: User, replies: List<PostReplyDto.PostReplyResponse>) : this(
            id = post.id,
            board = post.board.instrumentName,
            title = post.title,
            content = post.content,
            isAnonymous = post.isAnonymous,
            createAt = post.createdAt.toString(),
            updatedAt = post.updatedAt.toString(),
            isAuthor = post.author.id == user.id,
            nickname = post.authorNickname,
            replies = replies,
        )
    }

    data class NotAnonymousPostDetailResponse(
        override val id: Long,
        override val board: String,
        override val title: String,
        override val content: String,
        override val isAnonymous: Boolean,
        override val createAt: String,
        override val updatedAt: String,
        override val isAuthor: Boolean,
        val authorInfo: UserDto.SimpleUserDto,
        override val replies: List<PostReplyDto.PostReplyResponse>,
    ) : PostDetailResponse() {
        constructor(post: CommunityPost, user: User) : this(
            id = post.id,
            board = post.board.instrumentName,
            title = post.title,
            content = post.content,
            isAnonymous = post.isAnonymous,
            createAt = post.createdAt.toString(),
            updatedAt = post.updatedAt.toString(),
            isAuthor = post.author.id == user.id,
            authorInfo = UserDto.SimpleUserDto(post.author),
            replies = post.replies.map {
                if (it.isAnonymous) {
                    PostReplyDto.AnonymousPostReplyResponse(it, user)
                } else {
                    PostReplyDto.NotAnonymousPostReplyResponse(
                        it,
                        user,
                    )
                }
            },
        )

        constructor(post: CommunityPost, user: User, replies: List<PostReplyDto.PostReplyResponse>) : this(
            id = post.id,
            board = post.board.instrumentName,
            title = post.title,
            content = post.content,
            isAnonymous = post.isAnonymous,
            createAt = post.createdAt.toString(),
            updatedAt = post.updatedAt.toString(),
            isAuthor = post.author.id == user.id,
            authorInfo = UserDto.SimpleUserDto(post.author),
            replies = replies,
        )
    }
}
