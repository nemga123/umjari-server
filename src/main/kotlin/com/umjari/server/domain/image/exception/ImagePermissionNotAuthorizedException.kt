package com.umjari.server.domain.image.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.NotAllowedException

class ImagePermissionNotAuthorizedException :
    NotAllowedException(ErrorType.IMAGE_PERMISSION_NOT_AUTHORIZED, "Cannot remove not permitted image.")
