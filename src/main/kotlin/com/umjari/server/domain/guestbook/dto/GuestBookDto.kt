package com.umjari.server.domain.guestbook.dto

import com.umjari.server.domain.guestbook.model.GuestBook
import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.model.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class GuestBookDto {
    data class PostGuestBookRequest(
        @field:NotBlank
        val content: String?,
        @field:NotNull
        val private: Boolean,
    )

    data class GuestBookResponse(
        val userId: Long,
        val authorId: UserDto.SimpleUserDto,
        val content: String,
        val createdAt: String,
        val isAuthor: Boolean,
    ) {
        constructor(guestBook: GuestBook, currentUser: User?) : this(
            userId = guestBook.user.id,
            authorId = UserDto.SimpleUserDto(guestBook.user),
            content = guestBook.content,
            createdAt = guestBook.createdAt.toString(),
            isAuthor = guestBook.author.id == currentUser?.id
        )
    }
}
