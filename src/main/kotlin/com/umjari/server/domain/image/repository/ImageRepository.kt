package com.umjari.server.domain.image.repository

import com.umjari.server.domain.image.model.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ImageRepository : JpaRepository<Image, Long?> {
    @Query(
        """
            SELECT image FROM Image AS image JOIN FETCH image.owner WHERE image.token = :token
        """,
    )
    fun findByToken(@Param("token") token: String): Image?

    @Query(
        """
            SELECT image FROM Image AS image JOIN FETCH image.owner WHERE image.id = :id
        """,
    )
    fun findById(@Param("id") id: Int): Image?

    fun findAllByTokenIn(token: List<String>): List<Image>

    @Query(
        """
        SELECT image FROM Image AS image JOIN FETCH image.owner WHERE image.fileName = :fileName
    """,
    )
    fun findByFileName(@Param("fileName") fileName: String): Image?
}
