package com.umjari.server.domain.user.dto

import com.umjari.server.domain.group.dto.GroupDto
import com.umjari.server.domain.user.model.User
import jakarta.validation.constraints.NotBlank

class UserDto {
    data class SimpleUserDto(
        val id: Long,
        val name: String,
        val profileImage: String,
    ) {
        constructor(user: User) : this(
            id = user.id,
            name = user.name,
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

    data class UserInfoResponse(
        val nickname: String,
        val email: String,
        val intro: String?,
    ) {
        constructor(user: User) : this(
            nickname = user.nickname,
            email = user.email,
            intro = user.intro,
        )
    }
}
