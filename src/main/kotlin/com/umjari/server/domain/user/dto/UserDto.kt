package com.umjari.server.domain.user.dto

import com.umjari.server.domain.group.dto.GroupDto
import com.umjari.server.domain.user.model.User
import jakarta.validation.constraints.NotBlank

class UserDto {
    data class SimpleUserDto(
        val id: Long,
        val profileName: String,
        val profileImage: String,
    ) {
        constructor(user: User) : this(
            id = user.id,
            profileName = user.profileName,
            profileImage = user.profileImage,
        )
    }

    data class NicknameRequest(
        @field:NotBlank
        val nickname: String?,
    )

    data class UserGroupListResponse(
        val career: List<GroupDto.GroupUserResponse>,
    )

    data class ProfileImageRequest(
        @field:NotBlank
        val image: String?,
    )

    data class DetailUserInfoResponse(
        val id: Long,
        val profileName: String,
        val profileImage: String,
        val email: String,
        val intro: String?,
        val isSelfProfile: Boolean,
    ) {
        constructor(user: User, isSelfProfile: Boolean) : this(
            id = user.id,
            profileName = user.profileName,
            profileImage = user.profileImage,
            email = user.email,
            intro = user.intro,
            isSelfProfile = isSelfProfile,
        )
    }
}
