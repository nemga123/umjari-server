package com.umjari.server.domain.group.groupmusics.service

import com.umjari.server.domain.group.group.dto.GroupDto
import com.umjari.server.domain.group.group.exception.GroupIdNotFoundException
import com.umjari.server.domain.group.group.repository.GroupRepository
import com.umjari.server.domain.group.groupmusics.model.GroupMusic
import com.umjari.server.domain.group.groupmusics.repository.GroupMusicRepository
import com.umjari.server.domain.group.members.component.GroupMemberAuthorityValidator
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.music.exception.MusicIdNotFoundException
import com.umjari.server.domain.music.repository.MusicRepository
import com.umjari.server.domain.user.model.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupMusicService(
    private val groupRepository: GroupRepository,
    private val groupMusicRepository: GroupMusicRepository,
    private val musicRepository: MusicRepository,
    private val groupMemberAuthorityValidator: GroupMemberAuthorityValidator,
) {
    @Transactional
    fun updateConcertSetList(
        user: User,
        groupId: Long,
        updateGroupSetListRequest: GroupDto.UpdateGroupSetListRequest,
    ) {
        val group = groupRepository.findByIdOrNull(groupId)
            ?: throw GroupIdNotFoundException(groupId)

        groupMemberAuthorityValidator.checkMemberAuthorities(
            GroupMember.MemberRole.ADMIN,
            groupId,
            user.id,
        )

        val musicIds = updateGroupSetListRequest.musicIds

        if (musicIds.isEmpty()) {
            groupMusicRepository.deleteAllByGroupId(groupId)
        } else {
            groupMusicRepository.deleteAllByGroupIdAndMusicIdNotIn(groupId, musicIds)
            val musicList = musicRepository.findAllByIdIn(musicIds)
            val musicMap = musicList.associateBy { it.id }
            val registeredMusic = groupMusicRepository.findAllByGroupId(groupId)
            val registeredMusicIdSet = registeredMusic.map { it.music.id }.toSet()
            val groupSetList = musicIds.filter { musicId ->
                !registeredMusicIdSet.contains(musicId)
            }.map { id ->
                val music = musicMap[id] ?: throw MusicIdNotFoundException(id)
                GroupMusic(group = group, music = music)
            }
            groupMusicRepository.saveAll(groupSetList)
        }
    }
}
