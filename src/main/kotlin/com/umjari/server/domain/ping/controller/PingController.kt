package com.umjari.server.domain.ping.controller

import com.umjari.server.domain.ping.dto.PingDto
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "ping", description = "PingPong API")

@RestController
@RequestMapping("/api/v1/ping")
class PingController {
    @GetMapping("/")
    fun getPong(): PingDto.PingResponse {
        return PingDto.PingResponse()
    }
}