package com.umjari.server.domain.album.service

import com.umjari.server.domain.album.dto.AlbumDto
import com.umjari.server.domain.album.exception.AlbumIdNotFoundException
import com.umjari.server.domain.album.exception.DuplicatedUserAlbumTitleException
import com.umjari.server.domain.album.model.Album
import com.umjari.server.domain.album.repository.AlbumRepository
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.service.UserService
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AlbumService(
    private val albumRepository: AlbumRepository,
    private val userService: UserService,
) {
    fun createAlbum(createAlbumRequest: AlbumDto.CreateAlbumRequest, user: User) {
        if (albumRepository.existsByOwnerIdAndTitle(user.id, createAlbumRequest.title)) {
            throw DuplicatedUserAlbumTitleException(createAlbumRequest.title)
        }

        Album(
            title = createAlbumRequest.title,
            owner = user,
        ).also { album: Album ->  albumRepository.save(album) }
    }

    fun getAlbumListByProfileName(
        profileName: String,
        currentUser: User?,
        pageable: Pageable,
    ): AlbumDto.AlbumPageResponse {
        val owner = userService.getUserByProfileName(profileName)

        val albumListResponse = albumRepository.getAlbumsByOwnerProfileName(profileName, pageable).map {
            AlbumDto.AlbumSimpleResponse(it)
        }
        val albumPageResponse = PageResponse(albumListResponse, pageable.pageNumber)

        return AlbumDto.AlbumPageResponse(owner.id == currentUser?.id, albumPageResponse)
    }

    fun updateAlbumTitle(albumId: Long, updateAlbumRequest: AlbumDto.CreateAlbumRequest, user: User) {
        val album = getAlbumByIdAndOwnerId(albumId, user.id)

        if (albumRepository.existsByOwnerIdAndTitle(user.id, updateAlbumRequest.title)) {
            throw DuplicatedUserAlbumTitleException(updateAlbumRequest.title)
        }

        album.title = updateAlbumRequest.title
        albumRepository.save(album)
    }

    fun deleteAlbum(albumId: Long, user: User) {
        getAlbumByIdAndOwnerId(albumId, user.id).let { album: Album -> albumRepository.delete(album) }
    }

    fun getAlbumByIdAndOwnerId(albumId: Long, userId: Long): Album = albumRepository.findByIdAndOwnerId(albumId, userId)
            ?: throw AlbumIdNotFoundException(albumId)
}
