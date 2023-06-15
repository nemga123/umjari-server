package com.umjari.server.domain.user.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class UserIdNotFoundException(userId: Long) :
    DataNotFoundException(ErrorType.USER_ID_NOT_FOUND, "User id=$userId does not exist.")
