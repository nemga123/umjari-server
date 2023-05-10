package com.umjari.server.domain.user.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class UserProfileNameNotFoundException(profileName: String) :
    DataNotFoundException(ErrorType.USER_PROFILE_NAME_NOT_FOUND, "User name \"$profileName\" does not exist.")
