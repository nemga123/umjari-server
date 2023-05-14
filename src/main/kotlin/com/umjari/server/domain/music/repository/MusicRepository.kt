package com.umjari.server.domain.music.repository

import com.umjari.server.domain.music.model.Music
import org.springframework.data.jpa.repository.JpaRepository

interface MusicRepository : JpaRepository<Music, Long?>
