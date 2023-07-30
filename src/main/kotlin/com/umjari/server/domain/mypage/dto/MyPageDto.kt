package com.umjari.server.domain.mypage.dto

import com.umjari.server.domain.group.group.model.Group
import com.umjari.server.domain.groupqna.model.GroupQna
import com.umjari.server.domain.post.model.CommunityPost
import com.umjari.server.domain.post.model.CommunityPostReply
import com.umjari.server.domain.post.model.PostLike

class MyPageDto {
    data class MyPostListResponse(
        val id: Long,
        val board: String,
        val title: String,
        val replyCount: Int,
        val isAnonymous: Boolean,
        val likeCount: Int,
        val createdAt: String,
    ) {
        constructor(post: CommunityPost, likeList: List<PostLike>) : this(
            id = post.id,
            board = post.board.boardType,
            title = post.title,
            replyCount = post.replies.size,
            isAnonymous = post.isAnonymous,
            likeCount = likeList.size,
            createdAt = post.createdAt.toString(),
        )
    }
    data class SimplePostResponse(
        val id: Long,
        val board: String,
        val title: String,
    ) {
        constructor(post: CommunityPost) : this(
            id = post.id,
            board = post.board.boardType,
            title = post.title,
        )
    }

    data class MyPostReplyListResponse(
        val id: Long,
        val post: SimplePostResponse,
        val content: String,
        val createdAt: String,
        val likeCount: Int,
    ) {
        constructor(reply: CommunityPostReply) : this(
            id = reply.id,
            post = SimplePostResponse(reply.post),
            content = reply.content,
            createdAt = reply.createdAt.toString(),
            likeCount = reply.likes.size,
        )
    }

    data class SimpleGroupResponse(
        val id: Long,
        val name: String,
        val logo: String,
    ) {
        constructor(group: Group) : this(
            id = group.id,
            name = group.name,
            logo = group.logo,
        )
    }
    data class MyQnaListResponse(
        val id: Long,
        val group: SimpleGroupResponse,
        val title: String,
        val createdAt: String,
        val replyCount: Int,
    ) {
        constructor(qna: GroupQna) : this(
            id = qna.id,
            group = SimpleGroupResponse(qna.group),
            title = qna.title,
            createdAt = qna.createdAt.toString(),
            replyCount = qna.replies.size,
        )
    }
}
