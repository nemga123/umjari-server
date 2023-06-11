package com.umjari.server.domain.post.dto

import com.umjari.server.domain.post.model.CommunityPost
import com.umjari.server.domain.post.model.PostLike
import com.umjari.server.domain.post.model.PostReplyLike
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
        val board: BoardType,
        @field:NotBlank
        @field:Size(max = 255)
        val title: String?,
        @field:NotBlank
        val content: String?,
        @field:NotNull
        val isAnonymous: Boolean,
    )

    sealed class PostSimpleResponse {
        abstract val id: Long
        abstract val board: String
        abstract val title: String
        abstract val replyCount: Int
        abstract val isAnonymous: Boolean
        abstract val isAuthor: Boolean
        abstract val likeCount: Int
        abstract val isLiked: Boolean
    }

    data class AnonymousPostSimpleResponse(
        override val id: Long,
        override val board: String,
        override val title: String,
        override val replyCount: Int,
        override val isAnonymous: Boolean,
        override val isAuthor: Boolean,
        override val likeCount: Int,
        override val isLiked: Boolean,
        val nickname: String,
    ) : PostSimpleResponse() {
        constructor(post: CommunityPost, currentUser: User?, likeList: List<PostLike>) : this(
            id = post.id,
            board = post.board.boardType,
            title = post.title,
            replyCount = post.replies.size,
            isAnonymous = post.isAnonymous,
            isAuthor = post.author.id == currentUser?.id,
            likeCount = likeList.size,
            isLiked = likeList.any { it.user.id == currentUser?.id },
            nickname = post.authorNickname,
        )
    }

    data class NotAnonymousPostSimpleResponse(
        override val id: Long,
        override val board: String,
        override val title: String,
        override val replyCount: Int,
        override val isAnonymous: Boolean,
        override val isAuthor: Boolean,
        override val likeCount: Int,
        override val isLiked: Boolean,
        val authorInfo: UserDto.SimpleUserDto,
    ) : PostSimpleResponse() {
        constructor(post: CommunityPost, currentUser: User?, likeList: List<PostLike>) : this(
            id = post.id,
            board = post.board.boardType,
            title = post.title,
            replyCount = post.replies.size,
            isAnonymous = post.isAnonymous,
            isAuthor = post.author.id == currentUser?.id,
            likeCount = likeList.size,
            isLiked = likeList.any { it.user.id == currentUser?.id },
            authorInfo = UserDto.SimpleUserDto(post.author),
        )
    }

    sealed class PostDetailResponse {
        abstract val id: Long
        abstract val board: String
        abstract val title: String
        abstract val content: String
        abstract val isAnonymous: Boolean
        abstract val createAt: String
        abstract val updatedAt: String
        abstract val isAuthor: Boolean
        abstract val likeCount: Int
        abstract val isLiked: Boolean
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
        override val likeCount: Int,
        override val isLiked: Boolean,
        override val replies: List<PostReplyDto.PostReplyResponse>,
    ) : PostDetailResponse() {
        constructor(
            post: CommunityPost,
            user: User?,
            likeList: List<PostLike>,
            replyLikeList: Map<Long, List<PostReplyLike>>,
        ) : this(
            id = post.id,
            board = post.board.boardType,
            title = post.title,
            content = post.content,
            isAnonymous = post.isAnonymous,
            createAt = post.createdAt.toString(),
            updatedAt = post.updatedAt.toString(),
            isAuthor = post.author.id == user?.id,
            nickname = post.authorNickname,
            likeCount = likeList.size,
            isLiked = likeList.any { it.user.id == user?.id },
            replies = post.replies.map {
                if (it.isAnonymous) {
                    PostReplyDto.AnonymousPostReplyResponse(it, user, replyLikeList[it.id] ?: emptyList())
                } else {
                    PostReplyDto.NotAnonymousPostReplyResponse(
                        it,
                        user,
                        replyLikeList[it.id] ?: emptyList(),
                    )
                }
            },
        )

        constructor(post: CommunityPost, replies: List<PostReplyDto.PostReplyResponse>) : this(
            id = post.id,
            board = post.board.boardType,
            title = post.title,
            content = post.content,
            isAnonymous = post.isAnonymous,
            createAt = post.createdAt.toString(),
            updatedAt = post.updatedAt.toString(),
            isAuthor = true,
            nickname = post.authorNickname,
            isLiked = false,
            likeCount = 0,
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
        override val likeCount: Int,
        override val isLiked: Boolean,
        override val replies: List<PostReplyDto.PostReplyResponse>,
    ) : PostDetailResponse() {
        constructor(
            post: CommunityPost,
            user: User?,
            likeList: List<PostLike>,
            replyLikeList: Map<Long, List<PostReplyLike>>,
        ) : this(
            id = post.id,
            board = post.board.boardType,
            title = post.title,
            content = post.content,
            isAnonymous = post.isAnonymous,
            createAt = post.createdAt.toString(),
            updatedAt = post.updatedAt.toString(),
            isAuthor = post.author.id == user?.id,
            authorInfo = UserDto.SimpleUserDto(post.author),
            isLiked = likeList.any { it.user.id == user?.id },
            likeCount = likeList.size,
            replies = post.replies.map {
                if (it.isAnonymous) {
                    PostReplyDto.AnonymousPostReplyResponse(it, user, replyLikeList[it.id] ?: emptyList())
                } else {
                    PostReplyDto.NotAnonymousPostReplyResponse(
                        it,
                        user,
                        replyLikeList[it.id] ?: emptyList(),
                    )
                }
            },
        )

        constructor(post: CommunityPost, replies: List<PostReplyDto.PostReplyResponse>) : this(
            id = post.id,
            board = post.board.boardType,
            title = post.title,
            content = post.content,
            isAnonymous = post.isAnonymous,
            createAt = post.createdAt.toString(),
            updatedAt = post.updatedAt.toString(),
            isAuthor = true,
            isLiked = false,
            likeCount = 0,
            authorInfo = UserDto.SimpleUserDto(post.author),
            replies = replies,
        )
    }
}
