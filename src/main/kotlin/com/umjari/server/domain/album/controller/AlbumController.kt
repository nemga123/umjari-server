package com.umjari.server.domain.album.controller

import com.umjari.server.domain.album.dto.AlbumDto
import com.umjari.server.domain.album.service.AlbumService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Album", description = "앨범/사진 관련 APIs")
@RestController
@RequestMapping("/api/v1/album")
class AlbumController(
    private val albumService: AlbumService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAlbum(
        @Valid @RequestBody
        createAlbumRequest: AlbumDto.CreateAlbumRequest,
        @CurrentUser user: User,
    ) {
        albumService.createAlbum(createAlbumRequest, user)
    }

    @GetMapping("/profile-name/{profile_name}/")
    @ResponseStatus(HttpStatus.OK)
    fun getAlbum(
        @PathVariable("profile_name") profileName: String,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
        @CurrentUser user: User?,
    ): AlbumDto.AlbumPageResponse {
        return albumService.getAlbumListByProfileName(profileName, user, pageable)
    }

    @PutMapping("/{album_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAlbumTitle(
        @PathVariable("album_id") albumId: Long,
        @Valid @RequestBody
        createAlbumRequest: AlbumDto.CreateAlbumRequest,
        @CurrentUser user: User,
    ) {
        albumService.updateAlbumTitle(albumId, createAlbumRequest, user)
    }

    @DeleteMapping("/{album_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAlbum(
        @PathVariable("album_id") albumId: Long,
        @CurrentUser user: User,
    ) {
        albumService.deleteAlbum(albumId, user)
    }
}
