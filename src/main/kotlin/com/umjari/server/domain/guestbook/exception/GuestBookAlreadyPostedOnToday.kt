package com.umjari.server.domain.guestbook.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class GuestBookAlreadyPostedOnToday :
    InvalidRequestException(
        ErrorType.GUESTBOOK_ALREADY_POSTED_ON_TODAY,
        "GuestBook is already posted on today to this user.",
    )
