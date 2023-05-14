package com.umjari.server.domain.music.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Music APIs", description = "Music API")
@RestController
@RequestMapping("/api/v1/image")
class MusicController
