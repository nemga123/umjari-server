package com.umjari.server.domain.image.repository

import com.umjari.server.domain.image.model.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ImageRepository : JpaRepository<Image, Long?> {
    @Query(
        """
        SELECT image FROM Image AS image JOIN FETCH image.owner.userId WHERE image.token = :token
    """,
    )
    fun findByToken(@Param("token") token: String): Image?

    @Query(
        """
        SELECT image FROM Image AS image JOIN FETCH image.owner.userId WHERE image.fileName = :fileName
    """,
    )
    fun findByFileName(@Param("fileName") fileName: String): Image?
}
