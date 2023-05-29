package com.umjari.server.domain.album.exception

import com.umjari.server.global.exception.ErrorType
import com.umjari.server.global.exception.InvalidRequestException

class DuplicatedUserAlbumTitleException(albumTitle: String) :
    InvalidRequestException(ErrorType.DUPLICATED_USER_ALBUM_TITLE, "$albumTitle already exists")
