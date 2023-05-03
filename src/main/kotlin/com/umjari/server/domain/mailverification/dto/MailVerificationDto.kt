package com.umjari.server.domain.mailverification.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class MailVerificationDto {
    data class MailVerificationRequest(
        @field:NotBlank @field:Email
        val email: String?,
    )

    data class TokenValidationRequest(
        @field:NotBlank @field:Email
        val email: String?,
        @field:NotBlank
        @field:Size(min = 6, max = 6)
        val token: String?,
    )
}
