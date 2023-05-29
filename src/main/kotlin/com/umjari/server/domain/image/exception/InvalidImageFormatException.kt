package com.umjari.server.domain.image.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class InvalidImageFormatException(ext: String) :
    InvalidRequestException(ErrorType.INVALID_IMAGE_FORMAT, ".$ext extension is not allowed to upload.")
