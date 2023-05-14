package com.umjari.server.domain.music.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class MusicIdNotFoundException(musicId: Long) :
    DataNotFoundException(ErrorType.MUSIC_ID_NOT_FOUND, "musicId = $musicId is not found.")
