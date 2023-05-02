package com.umjari.server.domain.mailverification.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class InvalidTokenException :
    InvalidRequestException(ErrorType.INVALID_VERIFICATION_TOKEN, "Verification token is invalid.")
