package com.umjari.server.domain.concert.repository

import com.umjari.server.domain.concert.model.ConcertPerformer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ConcertPerformerRepository : JpaRepository<ConcertPerformer, Long?> {
    @Query(
        """
            SELECT Distinct u.user_id
                FROM umjari_user AS u
                LEFT OUTER JOIN
                    (SELECT * FROM concert_performer WHERE concert_music_id = :concertMusicId)
                     AS cp on u.id = cp.performer_id
                WHERE u.user_id IN (:userIds)
                    AND cp.concert_music_id is NULL
        """,
        nativeQuery = true,
    )
    fun findAllUserIdsNotEnrolled(
        @Param("userIds") userIds: Set<String>,
        @Param("concertMusicId") concertMusicId: Long,
    ): Set<String>

    fun deleteAllByConcertMusicIdAndPerformer_UserIdIn(concertMusicId: Long, performerUserId: List<String>)

    @Query(
        """
            SELECT concert_performer FROM ConcertPerformer AS concert_performer JOIN FETCH concert_performer.performer
                WHERE concert_performer.concertMusic.id = :concertMusicId
                ORDER BY concert_performer.performer.id
        """,
    )
    fun findParticipantsByConcertMusicId(@Param("concertMusicId") concertMusicId: Long): List<ConcertPerformer>
}
