package com.umjari.server.domain.group.repository

import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupMemberRepository : JpaRepository<GroupMember, Long?> {
    @Query(
        """
            SELECT Distinct u
                FROM User AS u
                LEFT OUTER JOIN
                    GroupMember AS gm on u.id = gm.user.id
                WHERE u.userId IN ('id')
                    AND (gm.group.id != 1 OR gm.group.id is NULL)
        """,
    )
    fun findAllUserIdsNotEnrolled(@Param("userIds") userIds: Set<String>, @Param("groupId") groupId: Long): Set<User>
}
