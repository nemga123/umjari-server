package com.umjari.server.domain.group.dto

class GroupRegisterDto {
    data class GroupRegisterRequest(
        val userIds: List<String>,
    )

    data class GroupRegisterResponse(
        val failedUsers: List<FailedUser>,
    )

    data class FailedUser(
        val userId: String,
        val reason: String,
    )
}
