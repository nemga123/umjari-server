package com.umjari.server.domain.auth.dto

import com.umjari.server.domain.user.model.User

class UserDto {
    data class UserInfoResponse(
        val nickname: String,
        val email: String,
        val phoneNumber: String,
        val intro: String?,
    ) {
        constructor(user: User) : this(
            nickname = user.nickname,
            email = user.email,
            phoneNumber = user.phoneNumber,
            intro = user.intro,
        )
    }
}
