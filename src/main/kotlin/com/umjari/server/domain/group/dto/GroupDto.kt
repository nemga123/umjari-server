package com.umjari.server.domain.group.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.PositiveOrZero

class GroupDto {
    data class CreateGroupRequest(
        @NotEmpty val name: String,
        @NotEmpty val logo: String = "default_image",
        @NotEmpty val practiceTime: String,
        val audition: Boolean,
        @PositiveOrZero val membershipFee: Int,
        @PositiveOrZero val monthlyFee: Int,
        @NotEmpty val regionParent: String,
        @NotEmpty val regionChild: String,
        @NotEmpty val regionDetail: String,
        val homepage: String?,
        val detailIntro: String?,
        val recruit: Boolean,
        val recruitDetail: String?,
    )
}
