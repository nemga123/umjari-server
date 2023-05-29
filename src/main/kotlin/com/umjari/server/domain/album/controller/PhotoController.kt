package com.umjari.server.domain.album.controller

import com.umjari.server.domain.album.dto.PhotoDto
import com.umjari.server.domain.album.service.PhotoService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Album", description = "앨범/사진 관련 APIs")
@RestController
@RequestMapping("/api/v1/album/{album_id}/photo")
class PhotoController(
    private val photoService: PhotoService,
) {
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    fun getPhotoList(
        @PathVariable("album_id") albumId: Long,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
        @CurrentUser user: User?,
    ): PhotoDto.PhotoListResponse {
        return photoService.getPhotoList(albumId, pageable, user)
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadPhotos(
        @PathVariable("album_id") albumId: Long,
        @RequestBody uploadPhotoRequest: PhotoDto.UploadPhotoRequest,
        @CurrentUser user: User,
    ): PhotoDto.UploadPhotoResponse {
        return photoService.uploadPhotos(albumId, uploadPhotoRequest, user)
    }

    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePhoto(
        @PathVariable("album_id") albumId: Long,
        @RequestBody deletePhotoRequest: PhotoDto.DeletePhotoRequest,
        @CurrentUser user: User,
    ) {
        photoService.deletePhotos(albumId, deletePhotoRequest, user)
    }
}
