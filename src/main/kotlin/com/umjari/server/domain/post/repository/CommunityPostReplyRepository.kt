package com.umjari.server.domain.post.repository

import com.umjari.server.domain.post.dto.BoardType
import com.umjari.server.domain.post.model.CommunityPostReply
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityPostReplyRepository : JpaRepository<CommunityPostReply, Long?> {
    fun getByPost_BoardAndPostIdAndId(board: BoardType, postId: Long, id: Long): CommunityPostReply?
}
