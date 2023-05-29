package com.umjari.server.domain.album.service

import com.umjari.server.domain.album.dto.AlbumDto
import com.umjari.server.domain.album.exception.AlbumIdNotFoundException
import com.umjari.server.domain.album.exception.DuplicatedUserAlbumTitleException
import com.umjari.server.domain.album.model.Album
import com.umjari.server.domain.album.repository.AlbumRepository
import com.umjari.server.domain.user.exception.UserProfileNameNotFoundException
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AlbumService(
    private val albumRepository: AlbumRepository,
    private val userRepository: UserRepository,
) {
    fun createAlbum(createAlbumRequest: AlbumDto.CreateAlbumRequest, user: User) {
        if (albumRepository.existsByOwnerIdAndTitle(user.id, createAlbumRequest.title)) {
            throw DuplicatedUserAlbumTitleException(createAlbumRequest.title)
        }

        val album = Album(
            title = createAlbumRequest.title,
            owner = user,
        )
        albumRepository.save(album)
    }

    fun getAlbumListByProfileName(
        profileName: String,
        currentUser: User?,
        pageable: Pageable,
    ): AlbumDto.AlbumPageResponse {
        val owner = userRepository.findByProfileName(profileName)
            ?: throw UserProfileNameNotFoundException(profileName)

        val albumList = albumRepository.getAlbumsByOwnerProfileName(profileName, pageable)
        val albumListResponse = albumList.map { AlbumDto.AlbumSimpleResponse(it) }
        val albumPageResponse = PageResponse(albumListResponse, pageable.pageNumber)

        return AlbumDto.AlbumPageResponse(owner.id == currentUser?.id, albumPageResponse)
    }

    fun updateAlbumTitle(albumId: Long, updateAlbumRequest: AlbumDto.CreateAlbumRequest, user: User) {
        val album = albumRepository.findByIdAndOwnerId(albumId, user.id)
            ?: throw AlbumIdNotFoundException(albumId)

        if (albumRepository.existsByOwnerIdAndTitle(user.id, updateAlbumRequest.title)) {
            throw DuplicatedUserAlbumTitleException(updateAlbumRequest.title)
        }

        album.title = updateAlbumRequest.title
        albumRepository.save(album)
    }

    fun deleteAlbum(albumId: Long, user: User) {
        val album = albumRepository.findByIdAndOwnerId(albumId, user.id)
            ?: throw AlbumIdNotFoundException(albumId)

        albumRepository.delete(album)
    }
}
