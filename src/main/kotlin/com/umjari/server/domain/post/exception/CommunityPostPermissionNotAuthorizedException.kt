package com.umjari.server.domain.post.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.NotAllowedException

class CommunityPostPermissionNotAuthorizedException :
    NotAllowedException(ErrorType.COMMUNITY_POST_PERMISSION_NOT_AUTHORIZED, "There is no permission to post.")
