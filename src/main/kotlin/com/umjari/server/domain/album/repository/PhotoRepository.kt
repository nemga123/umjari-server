package com.umjari.server.domain.album.repository

import com.umjari.server.domain.album.model.Photo
import org.springframework.data.jpa.repository.JpaRepository

interface PhotoRepository : JpaRepository<Photo, Long?>
