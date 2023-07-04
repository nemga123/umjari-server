package com.umjari.server.domain.auth.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.NotAllowedException

class UserIdMailForbiddenException :
    NotAllowedException(ErrorType.USER_ID_MAIL_FORBIDDEN, "User information request is wrong.")
