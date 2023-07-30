package com.umjari.server.domain.group.members.dto

import jakarta.validation.constraints.Pattern

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

    data class UpdateGroupMemberTimestampRequest(
        @field:Pattern(
            regexp = "^(\\d{4})-(\\d{2})-(\\d{2})$",
            message = "date format is 'YYYY-MM-DD'",
        )
        val joinedAt: String?,
        @field:Pattern(
            regexp = "^(\\d{4})-(\\d{2})-(\\d{2})$",
            message = "date format is 'YYYY-MM-DD'",
        )
        val leavedAt: String?,
    )
}
