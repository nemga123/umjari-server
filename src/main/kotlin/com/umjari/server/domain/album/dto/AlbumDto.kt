package com.umjari.server.domain.album.dto

import com.umjari.server.domain.album.model.Album
import com.umjari.server.global.pagination.PageResponse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class AlbumDto {
    data class CreateAlbumRequest(
        @field:NotBlank
        @field:Size(max = 255)
        val title: String,
    )

    data class AlbumPageResponse(
        val isAuthor: Boolean,
        val albumPage: PageResponse<AlbumSimpleResponse>,
    )

    data class AlbumSimpleResponse(
        val id: Long,
        val title: String,
        val headPhoto: String,
        val createAt: String,
        val photoCount: Int,
    ) {
        constructor(album: Album) : this(
            id = album.id,
            title = album.title,
            headPhoto = if (album.headPhoto != null) album.headPhoto!!.image.toUrl() else "default_image",
            createAt = album.createdAt.toString(),
            photoCount = album.photos.size,
        )
    }
}
