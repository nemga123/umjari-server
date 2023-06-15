package com.umjari.server.domain.friend.dto

import com.umjari.server.domain.friend.model.Friend
import com.umjari.server.domain.user.dto.UserDto
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.util.*

class FriendDto {
    data class PostFriendRequest(
        @field:NotNull @field:Positive
        val receiverId: Long?,
    )

    data class FriendResponse(
        val id: Long,
        val user: UserDto.SimpleUserDto,
    ) {
        constructor(friend: FriendInfoSqlInterface) : this(
            id = friend.id,
            user = UserDto.SimpleUserDto(
                id = friend.userId,
                profileName = friend.profileName,
                profileImage = friend.profileImage,
            ),
        )

        constructor(friend: Friend) : this(
            id = friend.id,
            user = UserDto.SimpleUserDto(friend.requester),
        )
    }

    interface FriendInfoSqlInterface {
        val id: Long
        val userId: Long
        val profileName: String
        val profileImage: String
        val createdAt: Date
    }
}
