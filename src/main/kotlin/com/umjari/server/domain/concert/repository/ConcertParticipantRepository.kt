package com.umjari.server.domain.concert.repository

import com.umjari.server.domain.concert.dto.ConcertParticipantDto
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

    @Query(
        """
            SELECT
                participant.concertMusic.concert.id AS  concertId,
                COUNT (DISTINCT participant.id) AS count
                FROM ConcertParticipant AS participant
                    JOIN participant.concertMusic
                WHERE participant.concertMusic.concert.id IN (:concertIds)
                    AND (participant.performer.id IN
                        (SELECT friend_relation.receiver.id
                        FROM Friend AS friend_relation
                        WHERE friend_relation.requester.id = :userId
                            AND friend_relation.status = 1)
                    OR participant.performer.id IN
                        (SELECT friend_relation.requester.id
                        FROM Friend AS friend_relation
                        WHERE friend_relation.receiver.id = :userId
                            AND friend_relation.status = 1))
                GROUP BY participant.concertMusic.concert.id
        """,
    )
    fun findFriendCount(
        @Param("concertIds") concertIds: Set<Long>,
        @Param("userId") userId: Long,
    ): List<ConcertParticipantDto.ConcertParticipatedInterface>

    @Query(
        """
            SELECT
                u AS performer,
                concert_performer.role AS role,
                concert_performer.part AS part
            FROM ConcertParticipant AS concert_performer JOIN User AS u ON concert_performer.performer.id = u.id
            WHERE concert_performer.concertMusic.concert.id = :concertId
            GROUP BY u, concert_performer.role, concert_performer.part
            ORDER BY u.id
        """,
    )
    fun findParticipantsByConcertId(
        @Param("concertId") concertId: Long,
    ): ArrayList<ConcertParticipantDto.ConcertParticipantSqlShortInterface>

    @Query(
        """
            SELECT concert_performer FROM ConcertParticipant AS concert_performer JOIN FETCH concert_performer.performer
                WHERE concert_performer.concertMusic.id = :concertMusicId
                ORDER BY concert_performer.performer.id
        """,
    )
    fun findParticipantsByConcertMusicId(@Param("concertMusicId") concertMusicId: Long): List<ConcertParticipant>

    @Query(
        """
            SELECT
                concert.id AS id,
                music.shortComposerEng AS shortComposerEng,
                music.nameEng AS nameEng,
                cp.part AS part,
                concert.concertDate AS concertDate,
                cm.id AS concertMusicId,
                cp.role AS role,
                cp.detailPart AS detailPart,
                group.name AS groupName
            FROM ConcertParticipant AS cp
                JOIN ConcertMusic AS cm ON cp.concertMusic.id = cm.id
                JOIN Music AS music ON cm.music.id = music.id
                JOIN Concert AS concert ON cm.concert.id = concert.id
                JOIN Group AS group ON concert.group.id = group.id
            WHERE
                cp.performer.id = :userId
        """,
    )
    fun findConcertListByJoinedUserId(
        @Param("userId") userId: Long,
    ): List<ConcertParticipantDto.ConcertHistorySqlSimpleInterface>

    @Query(
        """
            SELECT
                concert.id AS id,
                music.shortComposerEng AS shortComposerEng,
                music.nameEng AS nameEng,
                cp.part AS part,
                concert.posterImg AS concertPoster,
                concert.title AS title,
                cp.role AS role,
                concert.concertDate AS concertDate,
                concert.regionDetail AS regionDetail,
                cp.detailPart AS detailPart,
                group.name AS groupName
            FROM ConcertParticipant AS cp
                JOIN ConcertMusic AS cm ON cp.concertMusic.id = cm.id
                JOIN Music AS music ON cm.music.id = music.id
                JOIN Concert AS concert ON cm.concert.id = concert.id
                JOIN Group AS group ON concert.group.id = group.id
            WHERE
                cp.performer.id = :userId
            ORDER BY cm.id ASC
        """,
    )
    fun findConcertListByJoinedUserIdWithPoster(
        @Param("userId") userId: Long,
    ): List<ConcertParticipantDto.ConcertHistorySqlWithImageInterface>
}
