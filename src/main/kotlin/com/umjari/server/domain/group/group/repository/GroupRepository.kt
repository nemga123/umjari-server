package com.umjari.server.domain.group.group.repository

import com.umjari.server.domain.group.group.model.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupRepository : JpaRepository<Group, Long?>, JpaSpecificationExecutor<Group> {
    @Query(
        """
            SELECT group FROM Group AS group
                LEFT JOIN FETCH group.setList AS gm
                LEFT JOIN FETCH gm.music
            WHERE group.id = :groupId
        """,
    )
    fun findGroupFetchSetList(@Param("groupId") groupId: Long): Group?

    @Query(
        """
            SELECT group FROM Group AS group
                LEFT JOIN FETCH group.setList AS gm
                JOIN FETCH gm.music
            WHERE group.id IN (:groupIds)
        """,
    )
    fun findAllGroupsFetchSetListByIdIn(@Param("groupIds") groupIds: Set<Long>): List<Group>

    @Query(
        """
            SELECT group
                FROM Group AS group
                    JOIN FETCH group.region
                    LEFT JOIN FETCH group.members
            WHERE group.id IN (:groupIds)
        """,
    )
    fun findAllByIdsInWithRegionAndMemberList(
        @Param("groupIds") groupIds: Set<Long>,
    ): List<Group>
}
