package com.umjari.server.domain.concert.repository

import com.umjari.server.domain.concert.model.ConcertParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ConcertParticipantRepository : JpaRepository<ConcertParticipant, Long?> {
    @Query(
        """
            SELECT DISTINCT participant FROM ConcertParticipant AS participant JOIN FETCH participant.performer
                WHERE participant.performer.userId IN (:userIds)
                    AND participant.concertMusic.id = :concertMusicId
        """,
    )
    fun findAllAlreadyEnrolled(
        @Param("userIds") userIds: Set<String>,
        @Param("concertMusicId") concertMusicId: Long,
    ): Set<ConcertParticipant>

    fun deleteAllByConcertMusicIdAndPerformer_UserIdIn(concertMusicId: Long, performerUserId: List<String>)

    @Query(
        """
            SELECT concert_performer FROM ConcertParticipant AS concert_performer JOIN FETCH concert_performer.performer
                WHERE concert_performer.concertMusic.id = :concertMusicId
                ORDER BY concert_performer.performer.id
        """,
    )
    fun findParticipantsByConcertMusicId(@Param("concertMusicId") concertMusicId: Long): List<ConcertParticipant>
}
