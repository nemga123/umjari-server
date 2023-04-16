package com.umjari.server.domain.user.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class DuplicatedUserEmailException(email: String) :
    InvalidRequestException(ErrorType.DUPLICATED_USER_EMAIL, "$email already exists")
