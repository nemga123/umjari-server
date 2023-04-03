package com.umjari.server.domain.group.dto

import jakarta.validation.constraints.NotBlank

class GroupRegisterDto {
    data class GroupRegisterRequest(
        val userIds: List<String>,
    )

    data class GroupRegisterResponse(
        val failedUsers: List<FailedUser>,
    )

    data class FailedUser(
        @field:NotBlank
        val userId: String,
        @field:NotBlank
        val reason: String,
    )
}
