package com.umjari.server.domain.user.dto

import com.umjari.server.domain.user.model.User
import jakarta.validation.constraints.NotBlank

class UserDto {
    data class SimpleUserDto(
        val id: Long,
        val name: String,
    ) {
        constructor(user: User) : this(
            id = user.id,
            name = user.name,
        )
    }

    data class NicknameRequest(
        @field:NotBlank
        val nickname: String?,
    )
}
