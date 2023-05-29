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
    private val imageRepository: ImageRepository,
) {
    fun getPhotoList(albumId: Long, pageable: Pageable, user: User?): PhotoDto.PhotoListResponse {
        val album = albumRepository.findByIdOrNull(albumId)
            ?: throw AlbumIdNotFoundException(albumId)

        val photoList = photoRepository.getAllByAlbumId(albumId, pageable)
        val photoPageResponse = photoList.map { PhotoDto.PhotoResponse(it) }
        val pageResponse = PageResponse(
            photoPageResponse,
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
        val album = albumRepository.findByIdAndOwnerId(albumId, user.id)
            ?: throw AlbumIdNotFoundException(albumId)

        val failedImage = mutableListOf<PhotoDto.FailedImage>()
        val existImages = imageRepository.findAllByTokenIn(uploadPhotoRequest.tokenList)
        val existImageMap = existImages.associateBy { it.token }

        val photoObjectList = mutableListOf<Photo>()
        uploadPhotoRequest.tokenList.forEach {
            val image = existImageMap[it]
            if (image != null) {
                photoObjectList.add(
                    Photo(
                        album = album,
                        image = image,
                    ),
                )
            } else {
                failedImage.add(
                    PhotoDto.FailedImage(
                        token = it,
                        reason = "There is not image that token is $it",
                    ),
                )
            }
        }

        photoRepository.saveAll(photoObjectList)
        return PhotoDto.UploadPhotoResponse(failedImage)
    }

    @Transactional
    fun deletePhotos(albumId: Long, deletePhotoRequest: PhotoDto.DeletePhotoRequest, user: User) {
        photoRepository.deleteAllByAlbumIdAndAlbumOwnerIdAndIdIn(albumId, user.id, deletePhotoRequest.idList)
    }
}
