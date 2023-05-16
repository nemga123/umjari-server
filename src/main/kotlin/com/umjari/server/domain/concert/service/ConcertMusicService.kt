package com.umjari.server.domain.concert.service

import com.umjari.server.domain.concert.dto.ConcertPerformerDto
import com.umjari.server.domain.concert.exception.ConcertMusicIdNotFoundException
import com.umjari.server.domain.concert.model.ConcertPerformer
import com.umjari.server.domain.concert.repository.ConcertMusicRepository
import com.umjari.server.domain.concert.repository.ConcertPerformerRepository
import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.service.GroupMemberAuthorityService
import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConcertMusicService(
    private val concertMusicRepository: ConcertMusicRepository,
    private val concertPerformerRepository: ConcertPerformerRepository,
    private val userService: UserService,
    private val groupMemberAuthorityService: GroupMemberAuthorityService,
) {
    fun registerConcertParticipant(
        user: User,
        concertId: Long,
        concertMusicId: Long,
        registerConcertParticipantsRequest: ConcertPerformerDto.RegisterConcertParticipantsRequest,
    ): ConcertPerformerDto.RegisterConcertParticipantsResponse {
        val concertMusic = concertMusicRepository.findByConcertIdAndId(concertId, concertMusicId)
            ?: throw ConcertMusicIdNotFoundException(concertMusicId)

        groupMemberAuthorityService.checkMemberAuthorities(
            GroupMember.MemberRole.ADMIN,
            concertMusic.concert.group.id,
            user.id,
        )

        val requestUserIds = registerConcertParticipantsRequest.userIds.toMutableList()

        val failedUsers = mutableListOf<ConcertPerformerDto.FailedUser>()
        val (existingUserIds, userMap) = userService.getUserIdToUserMapInUserIds(requestUserIds)
        val notEnrolledUserIds = if (existingUserIds.isNotEmpty()) {
            concertPerformerRepository.findAllUserIdsNotEnrolled(
                existingUserIds,
                concertMusicId,
            )
        } else {
            emptySet()
        }
        notEnrolledUserIds.forEach { userId ->
            val concertPerformer = ConcertPerformer(
                concertMusic = concertMusic,
                performer = userMap[userId]!!,
            )
            concertPerformerRepository.save(concertPerformer)
        }

        val notExistingUserIds = requestUserIds.subtract(existingUserIds)
        notExistingUserIds.forEach {
            failedUsers.add(ConcertPerformerDto.FailedUser(it, "User does not exist."))
        }

        val alreadyEnrolledUserIds = existingUserIds.subtract(notEnrolledUserIds)
        alreadyEnrolledUserIds.forEach {
            failedUsers.add(ConcertPerformerDto.FailedUser(it, "User is already enrolled."))
        }

        return ConcertPerformerDto.RegisterConcertParticipantsResponse(failedUsers)
    }

    @Transactional
    fun removeConcertParticipant(
        user: User,
        concertId: Long,
        concertMusicId: Long,
        registerConcertParticipantsRequest: ConcertPerformerDto.RegisterConcertParticipantsRequest,
    ) {
        val concertMusic = concertMusicRepository.findByConcertIdAndId(concertId, concertMusicId)
            ?: throw ConcertMusicIdNotFoundException(concertMusicId)

        groupMemberAuthorityService.checkMemberAuthorities(
            GroupMember.MemberRole.ADMIN,
            concertMusic.concert.group.id,
            user.id,
        )

        concertPerformerRepository.deleteAllByConcertMusicIdAndPerformer_UserIdIn(
            concertMusicId,
            registerConcertParticipantsRequest.userIds,
        )
    }

    fun getConcertParticipantsList(
        user: User?,
        concertId: Long,
        concertMusicId: Long,
    ): ConcertPerformerDto.ConcertParticipantsListResponse {
        if (!concertMusicRepository.existsByConcertIdAndMusicId(concertId, concertMusicId)) {
            throw ConcertMusicIdNotFoundException(concertMusicId)
        }

        val concertParticipants = concertPerformerRepository.findParticipantsByConcertMusicId(concertMusicId)
        val participantDetailList = concertParticipants.map { concertPerformer ->
            UserDto.DetailUserInfoResponse(
                concertPerformer.performer,
                concertPerformer.performer.id == user?.id,
            )
        }

        return ConcertPerformerDto.ConcertParticipantsListResponse(participantDetailList)
    }
}
