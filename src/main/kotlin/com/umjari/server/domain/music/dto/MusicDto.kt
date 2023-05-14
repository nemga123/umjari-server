package com.umjari.server.domain.music.dto

import com.umjari.server.domain.music.model.Music
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class MusicDto {
    data class RegisterMusicRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val composerEng: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val composerKor: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val nameEng: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val nameKor: String?,
    )

    data class MusicDetailResponse(
        val id: Long,
        val composerEng: String,
        val composerKor: String,
        val nameEng: String,
        val nameKor: String,
    ) {
        constructor(music: Music) : this(
            id = music.id,
            composerEng = music.composerEng,
            composerKor = music.composerKor,
            nameEng = music.nameEng,
            nameKor = music.nameKor,
        )
    }

    data class MusicDetailListResponse(
        val musicList: List<MusicDetailResponse>,
        val counts: Int,
    )
}
