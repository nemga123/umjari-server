package com.umjari.server.domain.group.repository

import com.umjari.server.domain.group.model.GroupMusic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying

interface GroupMusicRepository : JpaRepository<GroupMusic, Long?> {
    fun findAllByGroupId(groupId: Long): List<GroupMusic>

    @Modifying
    fun deleteAllByGroupId(groupId: Long)

    @Modifying
    fun deleteAllByGroupIdAndMusicIdNotIn(groupId: Long, musicIds: MutableList<Long>)
}
