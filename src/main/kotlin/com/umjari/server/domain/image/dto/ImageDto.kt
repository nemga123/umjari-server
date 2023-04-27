package com.umjari.server.domain.image.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class ImageDto {
    data class ImageUrlResponse(
        val url: String,
        val token: String,
    )

    data class ImageTokenRequest(
        @field:NotNull
        @field:Size(max = 36)
        val token: String?,
    )
}
