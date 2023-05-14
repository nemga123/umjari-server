package com.umjari.server.domain.concert.repository

import com.umjari.server.domain.concert.model.ConcertMusic
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertMusicRepository : JpaRepository<ConcertMusic, Long?>
