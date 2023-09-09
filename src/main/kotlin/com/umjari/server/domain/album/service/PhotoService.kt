package com.umjari.server.domain.album.service

import com.umjari.server.domain.album.dto.PhotoDto
import com.umjari.server.domain.album.exception.AlbumIdNotFoundException
import com.umjari.server.domain.album.model.Photo
import com.umjari.server.domain.album.repository.AlbumRepository
import com.umjari.server.domain.album.repository.PhotoRepository
import com.umjari.server.domain.image.repository.ImageRepository
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PhotoService(
    private val photoRepository: PhotoRepository,
    private val albumRepository: AlbumRepository,
    private val albumService: AlbumService,
    private val imageRepository: ImageRepository,
) {
    fun getPhotoList(albumId: Long, pageable: Pageable, user: User?): PhotoDto.PhotoListResponse {
        val album = albumRepository.findByIdOrNull(albumId)
            ?: throw AlbumIdNotFoundException(albumId)

        val photoResponsePages = photoRepository.getAllByAlbumId(albumId, pageable)
            .map { PhotoDto.PhotoResponse(it) }
        val pageResponse = PageResponse(
            photoResponsePages,
            pageable.pageNumber,
        )
        return PhotoDto.PhotoListResponse(
            albumId = albumId,
            isAuthor = album.owner.id == user?.id,
            photoPage = pageResponse,
        )
    }

    fun uploadPhotos(
        albumId: Long,
        uploadPhotoRequest: PhotoDto.UploadPhotoRequest,
        user: User,
    ): PhotoDto.UploadPhotoResponse {
        val album = albumService.getAlbumByIdAndOwnerId(albumId, user.id)

        val existImageMap = imageRepository.findAllByTokenIn(uploadPhotoRequest.tokenList)
            .associateBy { it.token }

        val photoList = mutableListOf<Photo>()
        val failedPhotoList = mutableListOf<PhotoDto.FailedImage>()
        uploadPhotoRequest.tokenList.forEach { token: String ->
            val image = existImageMap[token]
            if (image != null) {
                photoList.add(
                    Photo(
                        album = album,
                        image = image,
                    ),
                )
            } else {
                failedPhotoList.add(
                    PhotoDto.FailedImage(
                        token = token,
                        reason = "There is not image that token is $token",
                    ),
                )
            }
        }

        photoRepository.saveAll(photoList)
        return PhotoDto.UploadPhotoResponse(failedPhotoList)
    }

    @Transactional
    fun deletePhotos(albumId: Long, deletePhotoRequest: PhotoDto.DeletePhotoRequest, user: User) {
        photoRepository.deleteAllByAlbumIdAndAlbumOwnerIdAndIdIn(albumId, user.id, deletePhotoRequest.idList)
    }
}
