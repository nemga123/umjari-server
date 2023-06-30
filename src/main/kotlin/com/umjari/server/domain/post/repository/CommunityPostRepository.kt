package com.umjari.server.domain.post.repository

import com.umjari.server.domain.post.dto.BoardType
import com.umjari.server.domain.post.model.CommunityPost
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommunityPostRepository : JpaRepository<CommunityPost, Long?> {
    @Query(
        value = """
            SELECT post
                FROM CommunityPost AS post
                    LEFT JOIN FETCH post.replies
                    JOIN FETCH post.author
            WHERE post.board = :board
        """,
        countQuery = """
            SELECT COUNT (post) FROM CommunityPost AS post WHERE post.board = :board
        """,
    )
    fun findByBoard(@Param("board") board: BoardType, pageable: Pageable): Page<CommunityPost>

    @Query(
        """
            SELECT post
                FROM CommunityPost AS post
                    LEFT JOIN FETCH post.replies
                    JOIN FETCH post.author
            WHERE post.board = :board AND post.id = :id
        """,
    )
    fun findByBoardAndId(@Param("board") board: BoardType, @Param("id") id: Long): CommunityPost?

    @Query(
        value = """
            SELECT post
                FROM CommunityPost AS post
                    LEFT JOIN FETCH post.replies
                    JOIN FETCH post.author
            WHERE post.author.id = :authorId
        """,
        countQuery = """
            SELECT COUNT (post) FROM CommunityPost AS post WHERE post.author.id = :authorId
        """,
    )
    fun findByAuthorId(@Param("authorId") authorId: Long, pageable: Pageable): Page<CommunityPost>

    @Query(
        value = """
            SELECT post
                FROM CommunityPost AS post
                    LEFT JOIN FETCH post.replies
                    JOIN FETCH post.author
            WHERE post.id IN (
                SELECT pl.post.id
                    FROM PostLike AS pl
                    WHERE pl.user.id = :userId
            )
        """,
        countQuery = """
            SELECT COUNT (pl) FROM PostLike AS pl WHERE pl.user.id = :userId
        """,
    )
    fun findAllLikedPosts(@Param("userId") userId: Long, pageable: Pageable): Page<CommunityPost>

    @Query(
        value = """
            SELECT post
                FROM CommunityPost AS post
                    LEFT JOIN FETCH post.replies
                    JOIN FETCH post.author
            WHERE post.id IN (
                SELECT reply.post.id
                    FROM CommunityPostReply AS reply
                    WHERE reply.author.id = :authorId AND reply.isDeleted = FALSE
            )
        """,
        countQuery = """
            SELECT COUNT (DISTINCT reply.post.id) FROM CommunityPostReply AS reply WHERE reply.author.id = :authorId
        """,
    )
    fun findAllRepliedPosts(@Param("authorId") authorId: Long, pageable: Pageable): Page<CommunityPost>
}
