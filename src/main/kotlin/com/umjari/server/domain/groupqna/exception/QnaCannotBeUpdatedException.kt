package com.umjari.server.domain.groupqna.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class QnaCannotBeUpdatedException(qnaId: Long) :
    InvalidRequestException(
        ErrorType.QNA_CANNOT_BE_UPDATED,
        "QnaId = $qnaId cannot be updated because reply is already commented",
    )
