package com.umjari.server.domain.concert.dto

import com.umjari.server.domain.concert.model.ConcertMusic
import com.umjari.server.domain.music.dto.MusicDto

class ConcertMusicDto {
    data class ConcertSetResponse(
        val id: Long,
        val musicInfo: MusicDto.MusicDetailResponse,
    ) {
        constructor(concertMusic: ConcertMusic) : this (
            id = concertMusic.id,
            musicInfo = MusicDto.MusicDetailResponse(concertMusic.music),
        )
    }
}
