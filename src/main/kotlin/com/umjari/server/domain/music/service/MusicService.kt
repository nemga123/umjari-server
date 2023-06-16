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
            shortComposerEng = registerMusicRequest.shortComposerEng!!,
            composerKor = registerMusicRequest.composerKor,
            shortComposerKor = registerMusicRequest.shortComposerKor!!,
            nameEng = registerMusicRequest.nameEng,
            shortNameEng = registerMusicRequest.shortNameEng!!,
            nameKor = registerMusicRequest.nameKor,
            shortNameKor = registerMusicRequest.shortNameKor!!,
        )
        musicRepository.save(musicObject)
        return MusicDto.MusicDetailResponse(musicObject)
    }

    fun getMusicList(
        composer: String,
        name: String,
    ): MusicDto.MusicDetailListResponse {
        val musicList = musicRepository.getMusicByFilterString(
            composer,
            name,
        )
        val musicListResponse = musicList.map { MusicDto.MusicDetailResponse(it) }
        return MusicDto.MusicDetailListResponse(musicListResponse, musicListResponse.size)
    }
}
