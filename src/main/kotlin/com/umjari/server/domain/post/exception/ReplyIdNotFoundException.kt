package com.umjari.server.domain.post.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class ReplyIdNotFoundException(replyId: Long) :
    DataNotFoundException(ErrorType.REPLY_ID_NOT_FOUND, "replyId = $replyId is not found.")
