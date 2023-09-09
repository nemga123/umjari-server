package com.umjari.server.domain.friend.service

import com.umjari.server.domain.friend.dto.FriendDto
import com.umjari.server.domain.friend.exception.AlreadyFriendRelationException
import com.umjari.server.domain.friend.exception.FriendAlreadyRequestedException
import com.umjari.server.domain.friend.exception.FriendRequestIdNotFoundException
import com.umjari.server.domain.friend.model.Friend
import com.umjari.server.domain.friend.repository.FriendRepository
import com.umjari.server.domain.user.exception.UserIdNotFoundException
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import com.umjari.server.domain.user.service.UserService
import com.umjari.server.global.pagination.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class FriendService(
    private val friendRepository: FriendRepository,
    private val userRepository: UserRepository,
    private val userService: UserService,
) {
    fun postFriendRequest(requestUser: User, postFriendRequest: FriendDto.PostFriendRequest) {
        val receiverId = postFriendRequest.receiverId!!
        if (friendRepository.findAlreadyRequested(requestUser.id, receiverId) != null) {
            throw FriendAlreadyRequestedException()
        }
        if (receiverId == requestUser.id) {
            throw AlreadyFriendRelationException()
        }

        val receiver = userRepository.findByIdOrNull(receiverId)
            ?: throw UserIdNotFoundException(receiverId)

        Friend(
            requester = requestUser,
            receiver = receiver,
            status = Friend.FriendshipStatus.FENDING,
        ).also { friend -> friendRepository.save(friend) }
    }

    fun approveFriendRequest(receiver: User, requestId: Long) {
        val request = friendRepository.findByIdAndReceiverIdAndStatus(
            requestId,
            receiver.id,
            Friend.FriendshipStatus.FENDING,
        )
            ?: throw FriendRequestIdNotFoundException(requestId)

        request.status = Friend.FriendshipStatus.APPROVED
        friendRepository.save(request)
    }

    fun rejectFriendRequest(receiver: User, requestId: Long) {
        val friendRequest = friendRepository.findByIdAndReceiverIdAndStatus(
            requestId,
            receiver.id,
            Friend.FriendshipStatus.FENDING,
        )
            ?: throw FriendRequestIdNotFoundException(requestId)

        friendRepository.delete(friendRequest)
    }

    fun deleteFriendRelation(currentUser: User, friendId: Long) {
        val friend = friendRepository.findFriendByIdAndUserId(friendId, currentUser.id)
            ?: throw FriendRequestIdNotFoundException(friendId)

        friendRepository.delete(friend)
    }

    fun getFriendRequestList(currentUser: User, pageable: Pageable): PageResponse<FriendDto.FriendResponse> {
        val pagedResponse = friendRepository.findAllFriendRequest(currentUser.id, pageable)
            .map { FriendDto.FriendResponse(it) }
        return PageResponse(pagedResponse, pageable.pageNumber)
    }

    fun getFriendList(profileName: String, pageable: Pageable): PageResponse<FriendDto.FriendResponse> {
        val user = userService.getUserByProfileName(profileName)
        val pagedResponse = friendRepository.findAllFriendByStatus(Friend.FriendshipStatus.APPROVED, user.id, pageable)
            .map { FriendDto.FriendResponse(it) }
        return PageResponse(pagedResponse, pageable.pageNumber)
    }
}
