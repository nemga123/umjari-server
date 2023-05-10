package com.umjari.server.domain.user.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class DuplicatedUserProfileNameException(profileName: String) :
    InvalidRequestException(ErrorType.DUPLICATED_USER_PROFILE_NAME, "$profileName already exists")
