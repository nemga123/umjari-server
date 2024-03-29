package com.umjari.server.domain.post.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class PostIdNotFoundException(postId: Long) :
    DataNotFoundException(ErrorType.POST_ID_NOT_FOUND, "postId = $postId is not found.")
