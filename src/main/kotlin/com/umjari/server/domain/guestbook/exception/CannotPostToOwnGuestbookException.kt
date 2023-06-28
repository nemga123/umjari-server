package com.umjari.server.domain.guestbook.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class CannotPostToOwnGuestbookException :
    InvalidRequestException(ErrorType.CANNOT_POST_TO_OWN_GUESTBOOK, "User cannot post to own guestbook.")
