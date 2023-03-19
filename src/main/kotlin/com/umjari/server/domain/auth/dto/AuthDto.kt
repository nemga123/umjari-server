package com.umjari.server.domain.auth.dto

class AuthDto {
    data class LogInRequest(
        val userId: String? = null,
        val password: String? = null,
    )

    data class SignUpRequest(
        val userId: String,
        val password: String,
        val email: String,
        val nickname: String,
        val intro: String? = null,
        val phoneNumber: String,
    )
}
