package com.umjari.server.domain.album.repository

import com.umjari.server.domain.album.model.Album
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AlbumRepository : JpaRepository<Album, Long?> {
    fun existsByOwnerIdAndTitle(ownerId: Long, title: String): Boolean

    fun findByIdAndOwnerId(id: Long, ownerId: Long): Album?

    @Query(
        value = """
            SELECT album FROM Album AS album
                LEFT JOIN FETCH album.photos
                JOIN FETCH album.owner
                WHERE album.owner.profileName = :profileName
        """,
        countQuery = """
            SELECT COUNT(album) FROM Album AS album WHERE album.owner.profileName = :profileName
        """,
    )
    fun getAlbumsByOwnerProfileName(@Param("profileName") profileName: String, pageable: Pageable): Page<Album>
}
