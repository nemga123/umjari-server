package com.umjari.server.domain.post.repository

import com.umjari.server.domain.post.model.PostLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

interface PostLikeRepository : JpaRepository<PostLike, Long?> {
    fun existsByUserIdAndPostId(userId: Long, postId: Long): Boolean

    @Modifying
    fun deleteByUserIdAndPostId(userId: Long, postId: Long)

    fun getAllByPostId(postId: Long): List<PostLike>

    fun getAllByPostIdIn(postIds: List<Long>): List<PostLike>
}
