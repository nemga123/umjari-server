package com.umjari.server.domain.user.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class DuplicatedUserNicknameException(nickname: String) :
    InvalidRequestException(ErrorType.DUPLICATED_USER_NICKNAME, "$nickname already exists")
