package com.umjari.server.domain.auth.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.NotAllowedException

class ResetPasswordForbiddenException :
    NotAllowedException(ErrorType.RESET_PASSWORD_FORBIDDEN, "User information request is wrong.")
