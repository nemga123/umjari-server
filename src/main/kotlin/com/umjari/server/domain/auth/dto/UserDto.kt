package com.umjari.server.domain.auth.dto

import com.umjari.server.domain.user.model.User

class UserDto {
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
