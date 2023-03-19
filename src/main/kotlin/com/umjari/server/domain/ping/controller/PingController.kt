package com.umjari.server.domain.ping.controller

import com.umjari.server.domain.ping.dto.PingDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {
    @GetMapping("/ping/")
    fun getPong(): PingDto.PingResponse {
        return PingDto.PingResponse()
    }
}