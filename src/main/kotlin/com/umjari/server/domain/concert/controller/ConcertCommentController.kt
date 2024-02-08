package com.umjari.server.domain.concert.controller

import com.umjari.server.domain.concert.dto.ConcertCommentDto
import com.umjari.server.domain.concert.service.ConcertCommentService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import com.umjari.server.global.pagination.PageResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Concert APIs", description = "콘서트 API")
@RestController
@RequestMapping("/api/v1/concert/{concert_id}/comments")
class ConcertCommentController(
    private val concertCommentService: ConcertCommentService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun createConcertComment(
        @PathVariable("concert_id") concertId: Long,
        @Valid @RequestBody
        creatConcertCommentRequest: ConcertCommentDto.CreateConcertCommentRequest,
        @CurrentUser user: User,
    ): ConcertCommentDto.ConcertCommentResponse {
        return concertCommentService.createConcertComment(user, creatConcertCommentRequest, concertId)
    }

    @PutMapping("/{comment_id}/")
    @ResponseStatus(HttpStatus.OK)
    fun createConcertComment(
        @PathVariable("concert_id") concertId: Long,
        @PathVariable("comment_id") commentId: Long,
        @Valid @RequestBody
        creatConcertCommentRequest: ConcertCommentDto.CreateConcertCommentRequest,
        @CurrentUser user: User,
    ): ConcertCommentDto.ConcertCommentResponse {
        return concertCommentService.updateConcertComment(user, creatConcertCommentRequest, concertId, commentId)
    }

    @DeleteMapping("/{comment_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteConcertComment(
        @PathVariable("concert_id") concertId: Long,
        @PathVariable("comment_id") commentId: Long,
        @CurrentUser user: User,
    ) {
        concertCommentService.deleteConcertComment(user, concertId, commentId)
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    fun getConcertCommentList(
        @PathVariable("concert_id") concertId: Long,
        @CurrentUser user: User,
        @PageableDefault(
            size = 20,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
    ): PageResponse<ConcertCommentDto.ConcertCommentResponse> {
        return concertCommentService.getConcertCommentList(user, concertId, pageable)
    }
}
