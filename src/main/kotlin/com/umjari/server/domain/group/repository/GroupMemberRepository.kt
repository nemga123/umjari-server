package com.umjari.server.domain.group.repository

import com.umjari.server.domain.group.model.GroupMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupMemberRepository : JpaRepository<GroupMember, Long?> {
    @Query(
        """
            SELECT Distinct u.user_id
                FROM umjari_user AS u
                LEFT OUTER JOIN
                    (SELECT * FROM group_member WHERE group_id = :groupId)
                     AS gm on u.id = gm.user_id
                WHERE u.user_id IN (:userIds)
                    AND gm.group_id is NULL
        """,
        nativeQuery = true,
    )
    fun findAllUserIdsNotEnrolled(@Param("userIds") userIds: Set<String>, @Param("groupId") groupId: Long): Set<String>

    fun findByGroup_IdAndUser_Id(groupId: Long, userId: Long): GroupMember?

    @Query(
        """
            SELECT groupMember FROM GroupMember AS groupMember JOIN FETCH groupMember.group
                WHERE groupMember.user.id = :userId
                ORDER BY groupMember.group.id ASC
        """,
    )
    fun findGroupListByUserId(@Param("userId") userId: Long): List<GroupMember>

    @Query(
        """
            SELECT DISTINCT gm FROM GroupMember AS gm JOIN FETCH gm.user AS user
                WHERE user.userId IN (:userIds) AND gm.group.id = :groupId
        """,
    )
    fun findAllAlreadyEnrolled(
        @Param("userIds") userIds: Set<String>,
        @Param("groupId") groupId: Long,
    ): Set<GroupMember>
}
