package com.umjari.server.domain.concert.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class ConcertCommentIdNotFoundException(concertCommentId: Long) :
    DataNotFoundException(ErrorType.CONCERT_COMMENT_ID_NOT_FOUND, "concertCommentId = $concertCommentId is not found.")
