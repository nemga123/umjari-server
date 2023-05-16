package com.umjari.server.domain.concert.dto

import com.umjari.server.domain.user.dto.UserDto

class ConcertPerformerDto {
    data class RegisterConcertParticipantsRequest(
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
        val participants: List<UserDto.DetailUserInfoResponse>,
    )
}
