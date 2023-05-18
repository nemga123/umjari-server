package com.umjari.server.domain.post.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.NotAllowedException

class ReplyPermissionNotAuthorizedException :
    NotAllowedException(ErrorType.REPLY_PERMISSION_NOT_AUTHORIZED, "There is no permission to reply.")
