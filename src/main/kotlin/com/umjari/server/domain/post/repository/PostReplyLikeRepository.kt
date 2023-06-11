package com.umjari.server.domain.post.repository

import com.umjari.server.domain.post.model.PostReplyLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

interface PostReplyLikeRepository : JpaRepository<PostReplyLike, Long?> {
    fun existsByUserIdAndReplyId(userId: Long, replyId: Long): Boolean

    @Modifying
    fun deleteByUserIdAndReplyId(userId: Long, replyId: Long)

    fun getAllByReplyIdIn(replyIds: List<Long>): List<PostReplyLike>
}
