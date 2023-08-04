package com.umjari.server.domain.auth.dto

import com.umjari.server.global.validation.KeyWordsBlock
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class AuthDto {
    data class LogInRequest(
        val userId: String? = null,
        val password: String? = null,
    )

    data class SignUpRequest(
        @field:NotBlank
        @field:Size(max = 255)
        @field:KeyWordsBlock
        val userId: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val password: String?,
        @field:NotBlank
        @field:Size(max = 255)
        @field:KeyWordsBlock
        val profileName: String?,
        @field:NotBlank @field:Email
        val email: String?,
        @field:NotBlank
        @field:Size(max = 255)
        @field:KeyWordsBlock
        val nickname: String?,
        @field:Size(max = 255)
        val intro: String? = null,
        @field:NotBlank
        @field:Size(max = 255)
        val profileImage: String? = "default_image",
        @field:NotNull
        @field:Size(max = 255)
        val regionParent: String? = "",
        @field:NotNull
        @field:Size(max = 255)
        val regionChild: String? = "",
    )

    data class FindPasswordRequest(
        @field:NotBlank
        @field:Size(max = 255)
        @field:KeyWordsBlock
        val userId: String?,
        @field:NotBlank @field:Email
        val email: String?,
    )

    data class UpdatePasswordRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val currentPassword: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val newPassword: String?,
    )

    data class UserIdMailRequest(
        @field:NotBlank @field:Email
        val email: String?,
    )
}
