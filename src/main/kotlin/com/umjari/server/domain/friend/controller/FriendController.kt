package com.umjari.server.domain.friend.controller

import com.umjari.server.domain.friend.dto.FriendDto
import com.umjari.server.domain.friend.service.FriendService
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Friend", description = "유저 친구 요청 관련 APIs")
@RestController
@RequestMapping("/api/v1/friend")
class FriendController(
    private val friendService: FriendService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    fun postFriendRequest(
        @Valid @RequestBody
        postFriendRequest: FriendDto.PostFriendRequest,
        @CurrentUser
        currentUser: User,
    ) {
        friendService.postFriendRequest(currentUser, postFriendRequest)
    }

    @PostMapping("/approval/{request_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun approveFriendRequest(
        @PathVariable("request_id") requestId: Long,
        @CurrentUser currentUser: User,
    ) {
        friendService.approveFriendRequest(currentUser, requestId)
    }

    @DeleteMapping("/rejection/{request_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun rejectFriendRequest(
        @PathVariable("request_id") requestId: Long,
        @CurrentUser currentUser: User,
    ) {
        friendService.rejectFriendRequest(currentUser, requestId)
    }

    @DeleteMapping("/{friend_id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFriendRelation(
        @PathVariable("friend_id") friendId: Long,
        @CurrentUser currentUser: User,
    ) {
        friendService.deleteFriendRelation(currentUser, friendId)
    }

    @GetMapping("/requests/")
    @ResponseStatus(HttpStatus.OK)
    fun getFriendRequestList(
        @CurrentUser currentUser: User,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
    ): PageResponse<FriendDto.FriendResponse> {
        return friendService.getFriendRequestList(currentUser, pageable)
    }
}
