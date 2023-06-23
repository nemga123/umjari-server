package com.umjari.server.domain.friend.repository

import com.umjari.server.domain.friend.dto.FriendDto
import com.umjari.server.domain.friend.model.Friend
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FriendRepository : JpaRepository<Friend, Long?> {
    @Query(
        """
            SELECT friend_relation
                FROM Friend AS friend_relation
                WHERE friend_relation.requester.id IN (:requestUserId, :receivedUserId)
                    AND friend_relation.receiver.id IN (:requestUserId, :receivedUserId)
        """,
    )
    fun findAlreadyRequested(
        @Param("requestUserId") requestUserId: Long,
        @Param("receivedUserId") receivedUserId: Long,
    ): Friend?

    fun findByIdAndReceiverIdAndStatus(id: Long, receiverId: Long, status: Friend.FriendshipStatus): Friend?

    @Query(
        """
            SELECT
                friend_relation.id AS id,
                friend_relation.receiver.id AS userId,
                friend_relation.receiver.profileName AS profileName,
                friend_relation.receiver.profileImage AS profileImage,
                friend_relation.createdAt AS createdAt
                    FROM Friend AS friend_relation
                    WHERE friend_relation.requester.id = :userId
                        AND friend_relation.status = :status
            UNION ALL
            SELECT
                friend_relation.id AS id,
                friend_relation.receiver.id AS userId,
                friend_relation.receiver.profileName AS profileName,
                friend_relation.receiver.profileImage AS profileImage,
                friend_relation.createdAt AS createdAt
                    FROM Friend AS friend_relation
                    WHERE friend_relation.receiver.id = :userId
                        AND friend_relation.status = :status
        """,
        countQuery = """
            SELECT COUNT (*) FROM Friend AS friend_relation
                WHERE (friend_relation.status =:status AND friend_relation.receiver.id = :userId)
                    OR (friend_relation.status =:status AND friend_relation.requester.id = :userId)
        """,
    )
    fun findAllFriendByStatus(
        @Param("status") status: Friend.FriendshipStatus,
        @Param("userId") userId: Long,
        pageable: Pageable,
    ): Page<FriendDto.FriendInfoSqlInterface>

    @Query(
        """
            SELECT friend_relation
                FROM Friend AS friend_relation
                WHERE (
                    friend_relation.id = :id
                    AND
                    (
                        friend_relation.receiver.id = :userId
                        OR
                        friend_relation.requester.id = :userId
                    )
                )
        """,
    )
    fun findFriendByIdAndUserId(@Param("id") id: Long, @Param("userId") userId: Long): Friend?

    @Query(
        """
            SELECT friend_relation
                FROM Friend AS friend_relation
                    JOIN FETCH friend_relation.requester
            WHERE friend_relation.receiver.id = :receiverId
                AND friend_relation.status = 0
        """,
        countQuery = """
            SELECT COUNT (*) FROM Friend AS friend_relation WHERE friend_relation.receiver.id = :receiverId AND friend_relation.status = 0
        """,
    )
    fun findAllFriendRequest(@Param("receiverId") receiverId: Long, pageable: Pageable): Page<Friend>

    @Query(
        """
            SELECT COUNT (*) > 0
                FROM Friend AS friend_relation
                WHERE friend_relation.requester.id IN (:currentUserId, :targetUserId)
                    AND friend_relation.receiver.id IN (:currentUserId, :targetUserId)
                    AND friend_relation.status = 1
        """,
    )
    fun isFriend(
        @Param("currentUserId") currentUSerId: Long,
        @Param("targetUserId") targetUserId: Long,
    ): Boolean
}
