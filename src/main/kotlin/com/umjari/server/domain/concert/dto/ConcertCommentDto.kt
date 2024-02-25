package com.umjari.server.domain.concert.dto

import com.umjari.server.domain.concert.model.ConcertComment
import com.umjari.server.domain.user.dto.UserDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class ConcertCommentDto {
    data class CreateConcertCommentRequest(
        @field:NotBlank
        @field:Size(max = 500)
        val comment: String,
    )

    data class ConcertCommentResponse(
        val id: Long,
        val simpleUserDto: UserDto.SimpleUserDto,
        val comment: String,
        val createAt: String,
        val isOwnedComment: Boolean,
    ) {
        constructor(concertComment: ConcertComment, isOwned: Boolean) : this(
            id = concertComment.id,
            simpleUserDto = UserDto.SimpleUserDto(concertComment.user),
            comment = concertComment.comment,
            createAt = concertComment.createdAt!!.toString(),
            isOwnedComment = isOwned,
        )
    }
}
