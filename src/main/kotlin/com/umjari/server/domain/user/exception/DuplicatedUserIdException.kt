package com.umjari.server.domain.user.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class DuplicatedUserIdException(userId: String) :
    InvalidRequestException(ErrorType.DUPLICATED_USER_ID, "$userId already exists")
