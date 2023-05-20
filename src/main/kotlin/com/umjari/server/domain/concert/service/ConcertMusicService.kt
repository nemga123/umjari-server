package com.umjari.server.domain.concert.service

import com.umjari.server.domain.concert.dto.ConcertParticipantDto
import com.umjari.server.domain.concert.exception.ConcertMusicIdNotFoundException
import com.umjari.server.domain.concert.model.ConcertParticipant
import com.umjari.server.domain.concert.repository.ConcertMusicRepository
import com.umjari.server.domain.concert.repository.ConcertParticipantRepository
import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.service.GroupMemberAuthorityService
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConcertMusicService(
    private val concertMusicRepository: ConcertMusicRepository,
    private val concertParticipantRepository: ConcertParticipantRepository,
    private val userService: UserService,
    private val groupMemberAuthorityService: GroupMemberAuthorityService,
) {
    fun registerConcertParticipant(
        user: User,
        concertId: Long,
        concertMusicId: Long,
        registerConcertParticipantListRequest: ConcertParticipantDto.RegisterConcertParticipantListRequest,
    ): ConcertParticipantDto.RegisterConcertParticipantsResponse {
        val concertMusic = concertMusicRepository.findByConcertIdAndId(concertId, concertMusicId)
            ?: throw ConcertMusicIdNotFoundException(concertMusicId)

        groupMemberAuthorityService.checkMemberAuthorities(
            GroupMember.MemberRole.ADMIN,
            concertMusic.concert.group.id,
            user.id,
        )

        val requestUserIds = registerConcertParticipantListRequest.participantList.map { it.userId }
        val requestUserIdToRole = registerConcertParticipantListRequest.participantList.associateBy { it.userId }
        val failedUsers = mutableListOf<ConcertParticipantDto.FailedUser>()
        val (existingUserIds, userMap) = userService.getUserIdToUserMapInUserIds(requestUserIds)
        val alreadyEnrolledUser = concertParticipantRepository.findAllAlreadyEnrolled(existingUserIds, concertMusicId)
        val alreadyEnrolledUserMap = alreadyEnrolledUser.associateBy { it.performer.userId }

        val objectList = existingUserIds.map { userId ->
            val participantRole = requestUserIdToRole[userId]!!
            if (alreadyEnrolledUserMap.containsKey(userId)) {
                val concertParticipant = alreadyEnrolledUserMap[userId]!!
                concertParticipant.part = participantRole.part
                concertParticipant.detailPart = participantRole.detailPart
                concertParticipant.role = participantRole.role
                concertParticipant
            } else {
                ConcertParticipant(
                    concertMusic = concertMusic,
                    performer = userMap[userId]!!,
                    part = participantRole.part,
                    detailPart = participantRole.detailPart,
                    role = participantRole.role,
                )
            }
        }

        concertParticipantRepository.saveAll(objectList)

        val notExistingUserIds = requestUserIds.subtract(existingUserIds)
        notExistingUserIds.forEach {
            failedUsers.add(ConcertParticipantDto.FailedUser(it, "User does not exist."))
        }

        return ConcertParticipantDto.RegisterConcertParticipantsResponse(failedUsers)
    }

    @Transactional
    fun removeConcertParticipant(
        user: User,
        concertId: Long,
        concertMusicId: Long,
        removeConcertParticipantListRequest: ConcertParticipantDto.RemoveConcertParticipantListRequest,
    ) {
        val concertMusic = concertMusicRepository.findByConcertIdAndId(concertId, concertMusicId)
            ?: throw ConcertMusicIdNotFoundException(concertMusicId)

        groupMemberAuthorityService.checkMemberAuthorities(
            GroupMember.MemberRole.ADMIN,
            concertMusic.concert.group.id,
            user.id,
        )

        concertParticipantRepository.deleteAllByConcertMusicIdAndPerformer_UserIdIn(
            concertMusicId,
            removeConcertParticipantListRequest.userIds,
        )
    }

    fun getConcertParticipantsList(
        concertId: Long,
        concertMusicId: Long,
    ): ConcertParticipantDto.ConcertParticipantsListResponse {
        if (!concertMusicRepository.existsByConcertIdAndId(concertId, concertMusicId)) {
            throw ConcertMusicIdNotFoundException(concertMusicId)
        }

        val concertParticipants = concertParticipantRepository.findParticipantsByConcertMusicId(concertMusicId)

        val partNameToParticipants = concertParticipants.groupBy { it.part }
        val concertParticipantByPartList = partNameToParticipants.map { (partName, partParticipants) ->
            val partResponse = ConcertParticipantDto.ConcertParticipantsByPartResponse(partName)
            partParticipants.forEach { concertParticipant ->
                partResponse.add(concertParticipant)
            }
            partResponse
        }
        return ConcertParticipantDto.ConcertParticipantsListResponse(concertParticipantByPartList)
    }
}
