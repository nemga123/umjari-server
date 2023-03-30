package com.umjari.server.domain.concert.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class ConcertNotFoundException(concertId: Long) :
    DataNotFoundException(ErrorType.CONCERT_ID_NOT_FOUND, "concertId = $concertId is not found.")
