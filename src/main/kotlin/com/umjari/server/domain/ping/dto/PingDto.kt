package com.umjari.server.domain.ping.dto

class PingDto {
    data class PingResponse(
        val pong: Boolean = true,
    )
}
