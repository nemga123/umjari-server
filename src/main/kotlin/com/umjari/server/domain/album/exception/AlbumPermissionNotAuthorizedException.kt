package com.umjari.server.domain.album.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.NotAllowedException

class AlbumPermissionNotAuthorizedException :
    NotAllowedException(ErrorType.ALBUM_PERMISSION_NOT_AUTHORIZED, "There is no permission to album.")
