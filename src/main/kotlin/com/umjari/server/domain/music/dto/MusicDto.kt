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
        val shortComposerEng: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val composerKor: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val shortComposerKor: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val nameEng: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val shortNameEng: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val nameKor: String?,
        @field:NotBlank
        @field:Size(max = 255)
        val shortNameKor: String?,
    )

    data class MusicDetailResponse(
        val id: Long,
        val composerEng: String,
        val shortComposerEng: String,
        val composerKor: String,
        val shortComposerKor: String,
        val nameEng: String,
        val shortNameEng: String,
        val nameKor: String,
        val shortNameKor: String,
    ) {
        constructor(music: Music) : this(
            id = music.id,
            composerEng = music.composerEng,
            shortComposerEng = music.shortComposerEng,
            composerKor = music.composerKor,
            shortComposerKor = music.shortComposerKor,
            nameEng = music.nameEng,
            shortNameEng = music.shortNameEng,
            nameKor = music.nameKor,
            shortNameKor = music.shortNameKor,
        )
    }

    data class MusicDetailListResponse(
        val musicList: List<MusicDetailResponse>,
        val counts: Int,
    )
}
