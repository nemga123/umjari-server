package com.umjari.server.domain.concert.repository

import com.umjari.server.domain.concert.model.Concert
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ConcertRepository : JpaRepository<Concert, Long?>, JpaSpecificationExecutor<Concert> {
    fun getConcertsByGroupId(groupId: Long, pageable: Pageable): Page<Concert>
}
