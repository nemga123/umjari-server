package com.umjari.server.domain.concert.controller

import com.umjari.server.domain.concert.dto.ConcertDto
import com.umjari.server.domain.concert.service.ConcertService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Concert APIs", description = "콘서트 API")
@RestController
@RequestMapping("/api/v1/concert")
class ConcertController(
    private val concertService: ConcertService,
) {
    @PostMapping("/group/{group_id}/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createConcert(
        @PathVariable("group_id") groupId: Long,
        @Valid @RequestBody
        createConcertRequest: ConcertDto.CreateConcertRequest,
    ): ConcertDto.ConcertDetailResponse {
        return concertService.createConcert(createConcertRequest, groupId)
    }

    @GetMapping("/{concert_id}/")
    @ResponseStatus(HttpStatus.OK)
    fun getConcert(@PathVariable("concert_id") concertId: Long): ConcertDto.ConcertDetailResponse {
        return concertService.getConcert(concertId)
    }
}
