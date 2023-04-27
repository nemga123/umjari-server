package com.umjari.server.domain.image.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class ImageTokenNotFoundException :
    DataNotFoundException(ErrorType.IMAGE_TOKEN_NOT_FOUND, "Image is not found.")
