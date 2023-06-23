package com.umjari.server.domain.guestbook.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class GuestBookIdNotFoundException(guestBookId: Long) :
    DataNotFoundException(ErrorType.GUEST_BOOK_ID_NOT_FOUND, "GuestBookId = $guestBookId is not found.")
