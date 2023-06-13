package com.umjari.server.domain.friendship.repository

import com.umjari.server.domain.friendship.model.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FriendshipRepository : JpaRepository<Friendship, Long?> {
    @Query(
        """
            SELECT friend_relation
                FROM Friendship AS friend_relation
                WHERE friend_relation.requester.id IN (:requestUserId, :receivedUserId)
                    AND friend_relation.receiver.id IN (:requestUserId, :receivedUserId)
        """,
    )
    fun findAlreadyRequested(
        @Param("requestUserId") requestUserId: Long,
        @Param("receivedUserId") receivedUserId: Long,
    ): Friendship?
}
