package com.umjari.server.domain.music.controller

import com.umjari.server.domain.music.dto.MusicDto
import com.umjari.server.domain.music.service.MusicService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Music APIs", description = "Music API")
@RestController
@RequestMapping("/api/v1/music")
class MusicController(
    private val musicService: MusicService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerMusic(
        @Valid @RequestBody
        registerMusicRequest: MusicDto.RegisterMusicRequest,
    ): MusicDto.MusicDetailResponse {
        return musicService.registerMusic(registerMusicRequest)
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    fun getMusicList(
        @RequestParam(required = false, defaultValue = "") composerEng: String,
        @RequestParam(required = false, defaultValue = "") composerKor: String,
        @RequestParam(required = false, defaultValue = "") nameEng: String,
        @RequestParam(required = false, defaultValue = "") nameKor: String,
    ): MusicDto.MusicDetailListResponse {
        return musicService.getMusicList(composerEng, composerKor, nameEng, nameKor)
    }
}
