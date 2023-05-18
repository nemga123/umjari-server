package com.umjari.server.domain.concert.dto

import com.umjari.server.domain.concert.model.ConcertParticipant
import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class ConcertParticipantDto {
    data class RegisterConcertParticipantListRequest(
        val participantList: List<RegisterConcertParticipantRequest>,
    )

    data class RegisterConcertParticipantRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val userId: String,
        @field:NotBlank
        @field:Size(max = 255)
        val part: String,
        @field:NotBlank
        @field:Size(max = 255)
        val detailPart: String,
        @field:NotNull
        val role: ConcertParticipant.ParticipantRole,
    )

    data class RemoveConcertParticipantListRequest(
        val userIds: List<String>,
    )

    data class RegisterConcertParticipantsResponse(
        val failedUsers: List<FailedUser>,
    )

    data class FailedUser(
        val userId: String,
        val reason: String,
    )

    data class ConcertParticipantsListResponse(
        val participants: List<ConcertParticipantResponse>,
    ) {
        constructor(concertParticipantList: List<ConcertParticipant>, currentUser: User?) : this(
            participants = concertParticipantList.map { ConcertParticipantResponse(it, currentUser) },
        )
    }

    data class ConcertParticipantResponse(
        val participant: UserDto.DetailUserInfoResponse,
        val part: String,
        val detailPart: String,
        val role: ConcertParticipant.ParticipantRole,
    ) {
        constructor(concertParticipant: ConcertParticipant, currentUser: User?) : this(
            participant = UserDto.DetailUserInfoResponse(concertParticipant.performer, currentUser),
            part = concertParticipant.part,
            detailPart = concertParticipant.detailPart,
            role = concertParticipant.role,
        )
    }
}
