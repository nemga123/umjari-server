package com.umjari.server.domain.image.exception

import com.umjari.server.global.exception.ConflictException
import com.umjari.server.global.exception.ErrorType

class ImageNotUploadedException :
    ConflictException(ErrorType.IMAGE_NOT_UPLOADED, "Image is not uploaded by S3 error.")
