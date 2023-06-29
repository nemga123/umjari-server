package com.umjari.server.domain.mypage.dto

import com.umjari.server.domain.post.model.CommunityPost
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
}
