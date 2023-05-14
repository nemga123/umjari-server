package com.umjari.server.domain.music.service

import com.umjari.server.domain.music.dto.MusicDto
import com.umjari.server.domain.music.model.Music
import com.umjari.server.domain.music.repository.MusicRepository
import org.springframework.stereotype.Service

@Service
class MusicService(
    private val musicRepository: MusicRepository,
) {
    fun registerMusic(registerMusicRequest: MusicDto.RegisterMusicRequest): MusicDto.MusicDetailResponse {
        val music = musicRepository.getByComposerEngAndComposerKorAndNameEngAndNameKor(
            registerMusicRequest.composerEng!!,
            registerMusicRequest.composerKor!!,
            registerMusicRequest.nameEng!!,
            registerMusicRequest.nameKor!!,
        )

        if (music != null) {
            return MusicDto.MusicDetailResponse(music)
        }

        val musicObject = Music(
            composerEng = registerMusicRequest.composerEng,
            composerKor = registerMusicRequest.composerKor,
            nameEng = registerMusicRequest.nameEng,
            nameKor = registerMusicRequest.nameKor,
        )
        musicRepository.save(musicObject)
        return MusicDto.MusicDetailResponse(musicObject)
    }

    fun getMusicList(
        composerEng: String,
        composerKor: String,
        nameEng: String,
        nameKor: String,
    ): MusicDto.MusicDetailListResponse {
        val musicList = musicRepository.getMusicByFilterString(
            composerEng,
            composerKor,
            nameEng,
            nameKor,
        )
        val musicListResponse = musicList.map { MusicDto.MusicDetailResponse(it) }
        return MusicDto.MusicDetailListResponse(musicListResponse, musicListResponse.size)
    }
}
