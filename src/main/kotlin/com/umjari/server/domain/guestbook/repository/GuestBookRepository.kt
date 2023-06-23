package com.umjari.server.domain.guestbook.repository

import com.umjari.server.domain.guestbook.model.GuestBook
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GuestBookRepository : JpaRepository<GuestBook, Long?> {
    @Query(
        """
            SELECT guest_book AS gb
                FROM GuestBook AS guest_book
                    JOIN FETCH guest_book.author
                WHERE guest_book.user.id = :userId
                    AND guest_book.private = false
            UNION ALL
            SELECT guest_book AS gb
                FROM GuestBook AS guest_book
                    JOIN FETCH guest_book.author
                WHERE guest_book.user.id = :userId
                    AND guest_book.private = true
                    AND guest_book.author.id = :authorId
        """,
        countQuery = """
            SELECT COUNT (*) FROM GuestBook AS guest_book
                WHERE (guest_book.user.id = :userId)
                    AND
                        (
                            (guest_book.private = true
                            AND guest_book.author.id = :authorId)
                            OR (guest_book.private = false)
                        )
        """,
    )
    fun findAllByUserIdWithAuthor(
        @Param("userId") userId: Long,
        @Param("authorId") authorId: Long,
        pageable: Pageable,
    ): Page<GuestBook>

    @Query(
        """
            SELECT guest_book AS gb
                FROM GuestBook AS guest_book
                    JOIN FETCH guest_book.author
                WHERE guest_book.user.id = :userId
                    AND guest_book.private = false
        """,
        countQuery = """
            SELECT COUNT (*)
                FROM GuestBook AS guest_book
                WHERE guest_book.user.id = :userId
                    AND guest_book.private = false
        """,
    )
    fun findAllByUserId(@Param("userId") userId: Long, pageable: Pageable): Page<GuestBook>

    fun findByIdAndAuthorId(id: Long, authorId: Long): GuestBook?
}
