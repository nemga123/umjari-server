package com.umjari.server.domain.guestbook.controller

import com.umjari.server.domain.guestbook.dto.GuestBookDto
import com.umjari.server.domain.guestbook.service.GuestBookService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Guest Book APIs", description = "Guest Book API")
@RestController
@RequestMapping("/api/v1/guestbook")
class GuestBookController(
    private val guestBookService: GuestBookService,
) {
    @PostMapping("/user/{user_id}/")
    @ResponseStatus(HttpStatus.CREATED)
    fun postGuestBook(
        @PathVariable("user_id") userId: Long,
        @Valid @RequestBody
        postGuestBookRequest: GuestBookDto.PostGuestBookRequest,
        @CurrentUser currentUser: User,
    ) {
        guestBookService.postGuestBook(userId, postGuestBookRequest, currentUser)
    }

    @PutMapping("/{guest_book_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateGuestBook(
        @PathVariable("guest_book_id") guestBookId: Long,
        @Valid @RequestBody
        updateGuestBookRequest: GuestBookDto.PostGuestBookRequest,
        @CurrentUser currentUser: User,
    ) {
        guestBookService.updateGuestBookPost(guestBookId, updateGuestBookRequest, currentUser)
    }

    @DeleteMapping("/{guest_book_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteGuestBook(
        @PathVariable("guest_book_id") guestBookId: Long,
        @CurrentUser currentUser: User,
    ) {
        guestBookService.deleteGuestBookPost(guestBookId, currentUser)
    }
}
