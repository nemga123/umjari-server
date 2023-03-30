package com.umjari.server.domain.concert.repository

import com.umjari.server.domain.concert.model.Concert
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertRepository : JpaRepository<Concert, Long?>
