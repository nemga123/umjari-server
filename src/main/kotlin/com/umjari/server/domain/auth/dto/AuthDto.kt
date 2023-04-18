package com.umjari.server.domain.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

class AuthDto {
    data class LogInRequest(
        val userId: String? = null,
        val password: String? = null,
    )

    data class SignUpRequest(
        @field:NotBlank
        val userId: String?,
        @field:NotBlank
        val password: String?,
        @field:NotBlank
        val name: String?,
        @field:NotBlank @field:Email
        val email: String?,
        @field:NotBlank
        val nickname: String?,
        val intro: String? = null,
        @field:Pattern(regexp = "^[0-9]{11}$")
        val phoneNumber: String?,
    )
}
