package com.umjari.server.domain.concert.dto

class ConcertPerformerDto {
    data class ConcertParticipantsRequest(
        val userIds: List<String>,
    )

    data class ConcertParticipantsResponse(
        val failedUsers: List<FailedUser>,
    )

    data class FailedUser(
        val userId: String,
        val reason: String,
    )
}
