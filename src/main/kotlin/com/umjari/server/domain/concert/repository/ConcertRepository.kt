package com.umjari.server.domain.concert.repository

import com.umjari.server.domain.concert.model.Concert
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertRepository : JpaRepository<Concert, Long?> {
    fun getConcertsByGroupId(groupId: Long, pageable: Pageable): Page<Concert>
}
