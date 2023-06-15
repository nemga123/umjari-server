package com.umjari.server.domain.friend.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class FriendRequestIdNotFoundException(requestId: Long) :
    DataNotFoundException(ErrorType.FRIEND_REQUEST_ID_NOT_FOUND, "Request Id=$requestId does not exist.")
