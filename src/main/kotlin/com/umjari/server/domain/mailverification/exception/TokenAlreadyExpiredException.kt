package com.umjari.server.domain.mailverification.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class TokenAlreadyExpiredException :
    InvalidRequestException(ErrorType.TOKEN_ALREADY_EXPIRED, "Token is already expired.")
