package com.umjari.server.domain.concert.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class DuplicatedUserConcertComment :
    InvalidRequestException(ErrorType.DUPLICATED_CONCERT_COMMENT, "Comment of this user already exists")
