package com.umjari.server.domain.auth.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class EmailNotVerifiedException :
    InvalidRequestException(ErrorType.NOT_VERIFIED_EMAIL, "Email is not verified.")
