package com.umjari.server.domain.post.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class BoardNameNotFoundException(boardName: String) :
    DataNotFoundException(ErrorType.BOARD_NAME_NOT_FOUND, "$boardName is not in Instrument enum.")
