package com.umjari.server.domain.friendship.service

import com.umjari.server.domain.friendship.repository.FriendshipRepository
import org.springframework.stereotype.Service

@Service
class FriendshipService(
    private val friendshipRepository: FriendshipRepository,
) {
//    fun postFriendRequest(requestUser: User, friendRequest: FriendshipDto.FriendRequest) {
//        val receiverId = friendRequest.id!!
//
//        if (receiverId == requestUser.id)
//    }
}
