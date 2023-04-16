package com.umjari.server.domain.user.dto

import com.umjari.server.domain.user.model.User
import jakarta.validation.constraints.NotBlank

class UserDto {
    data class SimpleUserDto(
        val id: Long,
        val nickname: String,
    ) {
        constructor(user: User) : this(
            id = user.id,
            nickname = user.nickname,
        )
    }

    data class NicknameRequest(
        @field:NotBlank
        val nickname: String?,
    )
}
