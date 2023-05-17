package com.umjari.server.domain.post.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class CommunityPostIdNotFoundException(postId: Long) :
    DataNotFoundException(ErrorType.COMMUNITY_POST_ID_NOT_FOUND, "postId = $postId is not found.")
