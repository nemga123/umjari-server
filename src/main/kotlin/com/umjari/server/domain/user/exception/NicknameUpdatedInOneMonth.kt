package com.umjari.server.domain.user.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.NotAllowedException

class NicknameUpdatedInOneMonth :
    NotAllowedException(ErrorType.NICKNAME_UPDATED_IN_ONE_MONTH, "Nickname can be updated only one time in month.")
