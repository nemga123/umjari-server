package com.umjari.server.domain.groupqna.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class QnaCannotBeDeletedException(qnaId: Long) :
    InvalidRequestException(
        ErrorType.QNA_CANNOT_BE_DELETED,
        "QnaId = $qnaId cannot be deleted because reply is already commented",
    )
