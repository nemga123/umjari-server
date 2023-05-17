package com.umjari.server.domain.post.repository

import com.umjari.server.domain.group.model.Instrument
import com.umjari.server.domain.post.model.CommunityPost
import org.springframework.data.jpa.repository.JpaRepository

interface CommunityPostRepository : JpaRepository<CommunityPost, Long?> {
    fun findByBoardAndId(board: Instrument, id: Long): CommunityPost?
}
