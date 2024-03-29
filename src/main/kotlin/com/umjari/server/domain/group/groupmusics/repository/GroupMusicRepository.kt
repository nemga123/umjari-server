package com.umjari.server.domain.group.groupmusics.repository

import com.umjari.server.domain.group.groupmusics.dto.GroupMusicDto
import com.umjari.server.domain.group.groupmusics.model.GroupMusic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GroupMusicRepository : JpaRepository<GroupMusic, Long?> {
    fun findAllByGroupId(groupId: Long): List<GroupMusic>

    @Modifying
    fun deleteAllByGroupId(groupId: Long)

    @Modifying
    fun deleteAllByGroupIdAndMusicIdNotIn(groupId: Long, musicIds: List<Long>)

    @Query(
        """
            SELECT gm FROM GroupMusic AS gm JOIN FETCH gm.music WHERE gm.group.id IN (:groupIds)
        """,
    )
    fun fetchGroupMusicByGroupIds(@Param("groupIds") groupIds: Set<Long>): List<GroupMusic>

    @Query(
        """
            SELECT
                gm.group.id AS groupId,
                COUNT (*) AS countMusic
            FROM GroupMusic AS gm
            WHERE gm.music.id in (:musicIds)
            GROUP BY gm.group.id
            HAVING COUNT (*) > 0
        """,
    )
    fun countGroupMusicsByMusicIdIn(
        @Param("musicIds") musicIds: List<Long>,
    ): List<GroupMusicDto.CountInterestMusicGroupQueryResultInterface>
}
