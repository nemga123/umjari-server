package com.umjari.server.domain.guestbook.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.NotAllowedException

class PrivateGuestBookNotAuthorizedException :
    NotAllowedException(ErrorType.PRIVATE_GUEST_BOOK_POST_NOT_AUTHORIZED, "Only friends can make a private guest book.")
