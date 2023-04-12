package com.umjari.server.domain.groupqna.dto

import jakarta.validation.constraints.NotBlank

class GroupQnaReplyDto {
    data class CreateReplyRequest(
        @field:NotBlank val content: String?,
    )

    data class ReplyResponse(
        val id: Long,
        val authorId: Long,

    )
}
