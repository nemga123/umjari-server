package com.umjari.server.domain.post.repository

import com.umjari.server.domain.post.dto.BoardType
import com.umjari.server.domain.post.model.CommunityPostReply
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommunityPostReplyRepository : JpaRepository<CommunityPostReply, Long?> {
    fun getByPost_BoardAndPostIdAndId(board: BoardType, postId: Long, id: Long): CommunityPostReply?

    @Query(
        """
            SELECT reply
                FROM CommunityPostReply AS reply
                    LEFT JOIN FETCH reply.likes
            WHERE reply.author.id = :userId AND reply.isDeleted = FALSE
        """,
        countQuery = """
            SELECT COUNT (*) FROM CommunityPostReply AS reply WHERE reply.author.id = :userID AND reply.isDeleted = FALSE
        """,
    )
    fun getAllMyReplies(@Param("userId") userId: Long, pageable: Pageable): Page<CommunityPostReply>
}
