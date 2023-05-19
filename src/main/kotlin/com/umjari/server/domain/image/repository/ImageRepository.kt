package com.umjari.server.domain.image.repository

import com.umjari.server.domain.image.model.Image
import org.springframework.data.jpa.repository.JpaRepository

interface ImageRepository : JpaRepository<Image, Long?> {
    fun findByToken(token: String): Image?

    fun findByFileName(fileName: String): Image?
}
