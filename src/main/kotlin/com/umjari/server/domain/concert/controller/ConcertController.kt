package com.umjari.server.domain.concert.controller

import com.umjari.server.domain.concert.dto.ConcertDto
import com.umjari.server.domain.concert.dto.ConcertParticipantDto
import com.umjari.server.domain.concert.service.ConcertService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import com.umjari.server.global.pagination.PageResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
        @CurrentUser user: User,
    ): ConcertDto.ConcertDetailResponse {
        return concertService.createConcert(user, createConcertRequest, groupId)
    }

    @GetMapping("/dashboard/")
    @ResponseStatus(HttpStatus.OK)
    fun getConcertDashboard(
        @RequestParam(required = false) startDate: String? = null,
        @RequestParam(required = false) endDate: String? = null,
        @RequestParam(required = false) regionParent: String? = null,
        @RequestParam(required = false) regionChild: String? = null,
        @RequestParam(required = false) text: String? = null,
        @PageableDefault(
            size = 20,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
    ): PageResponse<ConcertDto.ConcertSimpleResponse> {
        return concertService.getConcertDashboard(startDate, endDate, regionParent, regionChild, text, pageable)
    }

    @GetMapping("/{concert_id}/")
    @ResponseStatus(HttpStatus.OK)
    fun getConcert(@PathVariable("concert_id") concertId: Long): ConcertDto.ConcertDetailResponse {
        return concertService.getConcert(concertId)
    }

    @PutMapping("/{concert_id}/details/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateConcertDetail(
        @PathVariable("concert_id") concertId: Long,
        @Valid @RequestBody
        updateConcertDetailRequest: ConcertDto.UpdateConcertDetailRequest,
        @CurrentUser user: User,
    ) {
        concertService.updateConcertDetail(user, concertId, updateConcertDetailRequest)
    }

    @PutMapping("/{concert_id}/info/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateConcertInfo(
        @PathVariable("concert_id") concertId: Long,
        @Valid @RequestBody
        updateConcertInfoRequest: ConcertDto.UpdateConcertInfoRequest,
        @CurrentUser user: User,
    ) {
        concertService.updateConcertInfo(user, concertId, updateConcertInfoRequest)
    }

    @PutMapping("/{concert_id}/set-list/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateConcertSetList(
        @PathVariable("concert_id") concertId: Long,
        @Valid @RequestBody
        updateConcertSetListRequest: ConcertDto.UpdateConcertSetListRequest,
        @CurrentUser user: User,
    ) {
        concertService.updateConcertSetList(user, concertId, updateConcertSetListRequest)
    }

    @GetMapping("/{concert_id}/participant/")
    @ResponseStatus(HttpStatus.OK)
    fun getConcertSetParticipants(
        @PathVariable("concert_id") concertId: Long,
    ): ConcertParticipantDto.ConcertParticipantsListResponse {
        return concertService.getConcertParticipantsList(concertId)
    }
}
