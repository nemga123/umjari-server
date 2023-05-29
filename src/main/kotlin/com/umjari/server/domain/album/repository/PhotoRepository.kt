package com.umjari.server.domain.album.repository

import com.umjari.server.domain.album.model.Photo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PhotoRepository : JpaRepository<Photo, Long?> {
    @Query(
        value = """
            SELECT photo FROM Photo AS photo JOIN FETCH photo.image WHERE photo.album.id = :albumId
        """,
        countQuery = """
            SELECT COUNT (photo) FROM Photo AS photo WHERE photo.album.id = :albumId
        """,
    )
    fun getAllByAlbumId(@Param("albumId") albumId: Long, pageable: Pageable): Page<Photo>

    @Modifying
    fun deleteAllByAlbumIdAndAlbumOwnerIdAndIdIn(albumId: Long, albumOwnerId: Long, id: List<Long>)
}
