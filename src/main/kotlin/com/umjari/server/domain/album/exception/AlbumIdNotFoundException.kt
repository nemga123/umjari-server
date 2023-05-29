package com.umjari.server.domain.album.exception

import com.umjari.server.global.exception.DataNotFoundException
import com.umjari.server.global.exception.ErrorType

class AlbumIdNotFoundException(albumId: Long) :
    DataNotFoundException(ErrorType.ALBUM_ID_NOT_FOUND, "albumId = $albumId is not found.")
