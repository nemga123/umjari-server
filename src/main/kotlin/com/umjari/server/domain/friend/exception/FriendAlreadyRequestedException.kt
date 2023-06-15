package com.umjari.server.domain.friend.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class FriendAlreadyRequestedException :
    InvalidRequestException(ErrorType.FRIEND_ALREADY_REQUESTED, "This friend request is already sent.")
