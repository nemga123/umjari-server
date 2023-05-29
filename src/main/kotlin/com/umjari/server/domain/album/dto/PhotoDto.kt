package com.umjari.server.domain.album.dto

import com.umjari.server.domain.album.model.Photo
import com.umjari.server.global.pagination.PageResponse

class PhotoDto {
    data class UploadPhotoRequest(
        val tokenList: List<String>,
    )

    data class DeletePhotoRequest(
        val idList: List<Long>,
    )

    data class PhotoResponse(
        val id: Long,
        val createdAt: String,
        val url: String,
    ) {
        constructor(photo: Photo) : this(
            id = photo.id,
            createdAt = photo.createdAt.toString(),
            url = photo.image.toUrl(),
        )
    }

    data class PhotoListResponse(
        val albumId: Long,
        val isAuthor: Boolean,
        val photoPage: PageResponse<PhotoResponse>,
    )

    data class UploadPhotoResponse(
        val failedImage: List<FailedImage>,
    )

    data class FailedImage(
        val token: String,
        val reason: String,
    )
}
