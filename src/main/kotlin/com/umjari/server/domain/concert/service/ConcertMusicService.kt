package com.umjari.server.domain.concert.service

import com.umjari.server.domain.concert.dto.ConcertPerformerDto
import com.umjari.server.domain.concert.exception.ConcertMusicIdNotFoundException
import com.umjari.server.domain.concert.model.ConcertPerformer
import com.umjari.server.domain.concert.repository.ConcertMusicRepository
import com.umjari.server.domain.concert.repository.ConcertPerformerRepository
import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.service.GroupMemberAuthorityService
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConcertMusicService(
    private val concertMusicRepository: ConcertMusicRepository,
    private val concertPerformerRepository: ConcertPerformerRepository,
    private val userRepository: UserRepository,
    private val groupMemberAuthorityService: GroupMemberAuthorityService,
) {
    fun registerConcertParticipant(
        user: User,
        concertId: Long,
        concertMusicId: Long,
        concertParticipantsRequest: ConcertPerformerDto.ConcertParticipantsRequest,
    ): ConcertPerformerDto.ConcertParticipantsResponse {
        val concertMusic = concertMusicRepository.findByConcertIdAndId(concertId, concertMusicId)
            ?: throw ConcertMusicIdNotFoundException(concertMusicId)

        groupMemberAuthorityService.checkMemberAuthorities(
            GroupMember.MemberRole.ADMIN,
            concertMusic.concert.group.id,
            user.id,
        )

        val requestUserIds = concertParticipantsRequest.userIds.toMutableList()

        val failedUsers = mutableListOf<ConcertPerformerDto.FailedUser>()
        val existingUsers = userRepository.findUserIdsByUserIdIn(requestUserIds)
        val userMap = existingUsers.associateBy { it.userId }
        val existingUserIds = existingUsers.map { it.userId }.toSet()
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

        return ConcertPerformerDto.ConcertParticipantsResponse(failedUsers)
    }

    @Transactional
    fun removeConcertParticipant(
        user: User,
        concertId: Long,
        concertMusicId: Long,
        concertParticipantsRequest: ConcertPerformerDto.ConcertParticipantsRequest,
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
            concertParticipantsRequest.userIds,
        )
    }
}
