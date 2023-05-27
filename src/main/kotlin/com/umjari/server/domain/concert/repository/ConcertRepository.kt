package com.umjari.server.domain.concert.repository

import com.umjari.server.domain.concert.model.Concert
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ConcertRepository : JpaRepository<Concert, Long?>, JpaSpecificationExecutor<Concert> {
    @Query(
        value = """
            SELECT concert FROM Concert AS concert
                LEFT JOIN FETCH concert.playList AS concertMusic
                LEFT JOIN FETCH concertMusic.music
                JOIN FETCH concert.region
            WHERE concert.group.id = :groupId
        """,
        countQuery = """
            SELECT COUNT (concert) FROM Concert AS concert WHERE concert.group.id = :groupId
        """,
    )
    fun getConcertsByGroupId(@Param("groupId") groupId: Long, pageable: Pageable): Page<Concert>

    @Query(
        """
            SELECT concert FROM Concert AS concert
                LEFT JOIN FETCH concert.playList AS concertMusic
                LEFT JOIN FETCH concertMusic.music
                JOIN FETCH concert.region
            WHERE concert.id = :id
        """,
    )
    fun getConcertByIdFetchJoinConcertMusic(@Param("id") id: Long): Concert?
}
