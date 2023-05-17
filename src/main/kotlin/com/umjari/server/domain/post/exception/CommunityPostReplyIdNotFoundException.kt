package com.umjari.server.domain.post.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class CommunityPostReplyIdNotFoundException(replyId: Long):
    DataNotFoundException(ErrorType.COMMUNITY_POST_REPLY_ID_NOT_FOUND, "postId = $replyId is not found.")
