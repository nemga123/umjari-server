package com.umjari.server.domain.post.repository

import com.umjari.server.domain.group.model.Instrument
import com.umjari.server.domain.post.model.CommunityPost
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommunityPostRepository : JpaRepository<CommunityPost, Long?> {
    @Query(
        """
            SELECT post FROM CommunityPost AS post LEFT JOIN FETCH post.replies JOIN FETCH post.author
                WHERE post.board = :board AND post.id = :id
        """,
    )
    fun findByBoardAndId(@Param("board") board: Instrument, @Param("id") id: Long): CommunityPost?
}
