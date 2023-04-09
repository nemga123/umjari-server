package com.umjari.server.domain.user.dto

import com.umjari.server.domain.user.model.User

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
}
