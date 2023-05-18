package com.umjari.server.domain.user.dto

import com.umjari.server.domain.group.dto.GroupDto
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.validation.KeyWordsBlock
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

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
        @field:Size(max = 255)
        val image: String?,
    )

    data class DetailUserInfoResponse(
        val id: Long,
        val profileName: String,
        val profileImage: String,
        val nickname: String?,
        val email: String,
        val intro: String?,
        val isSelfProfile: Boolean,
        val career: List<GroupDto.GroupUserResponse>,
    ) {
        constructor(user: User, currentUser: User?) : this(
            id = user.id,
            profileName = user.profileName,
            profileImage = user.profileImage,
            email = user.email,
            intro = user.intro,
            isSelfProfile = user.id == currentUser?.id,
            nickname = if (user.id == currentUser?.id) user.nickname else null,
            career = user.career.map { GroupDto.GroupUserResponse(it) },
        )
    }

    data class UpdateUserInfoRequest(
        @field:NotBlank
        @field:Size(max = 255)
        @field:KeyWordsBlock
        val profileName: String?,
        @field:NotBlank
        @field:Size(max = 255)
        @field:KeyWordsBlock
        val nickname: String?,
        @field:Size(max = 255)
        val intro: String? = null,
    )
}
