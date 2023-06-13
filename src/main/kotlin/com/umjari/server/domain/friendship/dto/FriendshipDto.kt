package com.umjari.server.domain.friendship.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

class FriendshipDto {
    data class FriendRequest(
        @field:NotNull @field:Positive
        val id: Long?,
    )
}
