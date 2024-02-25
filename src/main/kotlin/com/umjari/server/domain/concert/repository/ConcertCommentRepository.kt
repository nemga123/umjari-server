package com.umjari.server.domain.concert.repository

import com.umjari.server.domain.concert.model.ConcertComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ConcertCommentRepository : JpaRepository<ConcertComment, Long> {
    fun existsConcertCommentByUserIdAndConcertId(userId: Long, concertId: Long): Boolean

    fun findConcertCommentByIdAndUserIdAndConcertId(id: Long, userId: Long, concertId: Long): ConcertComment?

    fun deleteConcertCommentByIdAndUserIdAndConcertId(id: Long, userId: Long, concertId: Long): ConcertComment

    @Query(
        value = """
            SELECT concertComment FROM ConcertComment AS concertComment JOIN FETCH concertComment.user WHERE concertComment.concert.id = :concertId
        """,
        countQuery = """
            SELECT COUNT (concertComment) FROM ConcertComment AS concertComment WHERE concertComment.concert.id = :concertId
        """,
    )
    fun getAllByConcertId(@Param("concertId") concertId: Long, pageable: Pageable): Page<ConcertComment>
}
