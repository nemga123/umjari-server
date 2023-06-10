package com.umjari.server.domain.post.repository

import com.umjari.server.domain.post.model.PostReplyLike
import org.springframework.data.jpa.repository.JpaRepository

interface PostReplyLikeRepository : JpaRepository<PostReplyLike, Long?>
