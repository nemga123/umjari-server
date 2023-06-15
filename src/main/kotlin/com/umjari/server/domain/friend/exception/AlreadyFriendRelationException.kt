package com.umjari.server.domain.friend.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class AlreadyFriendRelationException :
    InvalidRequestException(ErrorType.ALREADY_FRIEND_RELATION, "Requested users are already friend")
