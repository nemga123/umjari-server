package com.umjari.server.domain.auth.dto

class TokenDto {
    data class TokenResponse(
        val accessToken: String,
        val refreshToken: String,
    )
}