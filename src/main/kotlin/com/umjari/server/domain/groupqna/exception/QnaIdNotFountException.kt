package com.umjari.server.domain.groupqna.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class QnaIdNotFountException(groupId: Long, qnaId: Long) :
    DataNotFoundException(ErrorType.QNA_ID_NOT_FOUND, "QnaId = $qnaId is not found in group $groupId.")
