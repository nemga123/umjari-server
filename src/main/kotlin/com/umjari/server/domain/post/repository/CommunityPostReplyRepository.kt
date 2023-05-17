package com.umjari.server.domain.post.repository

import com.umjari.server.domain.group.model.Instrument
import com.umjari.server.domain.post.model.CommunityPostReply
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityPostReplyRepository : JpaRepository<CommunityPostReply, Long?> {
    fun getByPost_BoardAndPostIdAndId(board: Instrument, postId: Long, id: Long): CommunityPostReply?
}
