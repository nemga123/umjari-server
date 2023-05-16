package com.umjari.server.domain.concert.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class ConcertMusicIdNotFoundException(concertMusicId: Long) :
    DataNotFoundException(ErrorType.CONCERT_MUSIC_ID_NOT_FOUND, "concertMusicId = $concertMusicId is not found.")
