package com.umjari.server.domain.groupqna.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class QnaReplyIdNotFoundException(replyId: Long) :
    DataNotFoundException(ErrorType.QNA_REPLY_ID_NOT_FOUND, "ReplyId = $replyId is not found.")
