package com.umjari.server.domain.concert.service

import com.umjari.server.domain.concert.dto.ConcertParticipantDto
import com.umjari.server.domain.concert.exception.ConcertMusicIdNotFoundException
import com.umjari.server.domain.concert.model.ConcertParticipant
import com.umjari.server.domain.concert.repository.ConcertMusicRepository
import com.umjari.server.domain.concert.repository.ConcertParticipantRepository
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.group.members.component.GroupMemberAuthorityValidator
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConcertMusicService(
    private val concertMusicRepository: ConcertMusicRepository,
    private val concertParticipantRepository: ConcertParticipantRepository,
    private val userService: UserService,
    private val groupMemberAuthorityValidator: GroupMemberAuthorityValidator,
) {
    fun registerConcertParticipant(
        currentUser: User,
        concertId: Long,
        concertMusicId: Long,
        registerConcertParticipantListRequest: ConcertParticipantDto.RegisterConcertParticipantListRequest,
    ): ConcertParticipantDto.UpdateConcertParticipantsResponse {
        val concertMusic = concertMusicRepository.findByConcertIdAndId(concertId, concertMusicId)
            ?: throw ConcertMusicIdNotFoundException(concertMusicId)

        // check currentUser is Admin user
        groupMemberAuthorityValidator.checkMemberAuthorities(
            GroupMember.MemberRole.ADMIN,
            concertMusic.concert.group.id,
            currentUser.id,
        )

        val requestUserIds = registerConcertParticipantListRequest.participantList.map { it.userId }.toSet()
        val requestUserIdToRole = registerConcertParticipantListRequest.participantList.associateBy { it.userId }
        val (existingUserIds, userMap) = userService.getUserIdToUserMapInUserIds(requestUserIds)
        val alreadyEnrolledUserMap = concertParticipantRepository.findAllAlreadyEnrolled(existingUserIds, concertMusicId).associateBy { it.performer.userId }

        existingUserIds.map { userId ->
            val participantRole = requestUserIdToRole.getValue(userId)
            if (alreadyEnrolledUserMap.containsKey(userId)) {
                val concertParticipant = alreadyEnrolledUserMap.getValue(userId)
                concertParticipant.part = participantRole.part
                concertParticipant.detailPart = participantRole.detailPart
                concertParticipant.role = participantRole.role
                concertParticipant
            } else {
                ConcertParticipant(
                    concertMusic = concertMusic,
                    performer = userMap.getValue(userId),
                    part = participantRole.part,
                    detailPart = participantRole.detailPart,
                    role = participantRole.role,
                )
            }
        }.let { concertParticipants ->  concertParticipantRepository.saveAll(concertParticipants) }

        val notExistingUserIds = requestUserIds.subtract(existingUserIds)

        val failedUsers = notExistingUserIds.map {
            ConcertParticipantDto.FailedUser(it, "User does not exist.")
        }

        return ConcertParticipantDto.UpdateConcertParticipantsResponse(failedUsers)
    }

    @Transactional
    fun removeConcertParticipant(
        user: User,
        concertId: Long,
        concertMusicId: Long,
        removeConcertParticipantListRequest: ConcertParticipantDto.RemoveConcertParticipantListRequest,
    ): ConcertParticipantDto.UpdateConcertParticipantsResponse {
        val concertMusic = concertMusicRepository.findByConcertIdAndId(concertId, concertMusicId)
            ?: throw ConcertMusicIdNotFoundException(concertMusicId)

        groupMemberAuthorityValidator.checkMemberAuthorities(
            GroupMember.MemberRole.ADMIN,
            concertMusic.concert.group.id,
            user.id,
        )

        val userIds = removeConcertParticipantListRequest.userIds.toSet()
        val enrolledUser = concertParticipantRepository.findAllAlreadyEnrolled(userIds, concertMusicId)
            .also { concertParticipantRepository.deleteAll(it) }
        val enrolledUserIds = enrolledUser.map { it.performer.userId }.toSet()
        val failedUsers = userIds.filter{ userId -> !enrolledUserIds.contains(userId) }.map{ userId ->
            ConcertParticipantDto.FailedUser(userId, "User does not enrolled in concert.")
        }

        return ConcertParticipantDto.UpdateConcertParticipantsResponse(failedUsers)
    }

    fun getConcertParticipantsList(
        concertId: Long,
        concertMusicId: Long,
    ): ConcertParticipantDto.ConcertParticipantsListResponse {
        if (!concertMusicRepository.existsByConcertIdAndId(concertId, concertMusicId)) {
            throw ConcertMusicIdNotFoundException(concertMusicId)
        }

        val partNameToParticipants = concertParticipantRepository.findParticipantsByConcertMusicId(concertMusicId)
            .groupBy { it.part }
        val concertParticipantByPartList = partNameToParticipants.map { (partName, partParticipants) ->
            ConcertParticipantDto.ConcertParticipantsByPartResponse(partName, partParticipants)
        }
        return ConcertParticipantDto.ConcertParticipantsListResponse(concertParticipantByPartList)
    }
}
