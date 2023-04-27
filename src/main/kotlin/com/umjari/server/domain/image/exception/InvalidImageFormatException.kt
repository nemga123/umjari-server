package com.umjari.server.domain.image.exception

import com.umjari.server.global.exception.ConflictException
import com.umjari.server.global.exception.ErrorType

class InvalidImageFormatException(ext: String) :
    ConflictException(ErrorType.INVALID_IMAGE_FORMAT, ".$ext extension is not allowed to upload.")
