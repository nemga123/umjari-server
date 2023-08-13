package com.umjari.server.domain.user.controller

import com.umjari.server.domain.concert.dto.ConcertParticipantDto
import com.umjari.server.domain.friend.dto.FriendDto
import com.umjari.server.domain.friend.service.FriendService
import com.umjari.server.domain.guestbook.dto.GuestBookDto
import com.umjari.server.domain.guestbook.service.GuestBookService
import com.umjari.server.domain.music.dto.MusicDto
import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.service.UserService
import com.umjari.server.global.auth.annotation.CurrentUser
import com.umjari.server.global.pagination.PageResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "user", description = "유저 관련 APIs")
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
    private val friendService: FriendService,
    private val guestBookService: GuestBookService,
) {
    @GetMapping("/my-group/")
    @ResponseStatus(HttpStatus.OK)
    fun getMyGroupList(@CurrentUser user: User): UserDto.UserGroupListResponse {
        return userService.getJoinedGroupList(user)
    }

    @GetMapping("/profile-name/{profile_name}/joined-concert/")
    @ResponseStatus(HttpStatus.OK)
    fun getJoinedConcertList(
        @PathVariable("profile_name") profileName: String,
    ): ConcertParticipantDto.ParticipatedConcertListResponse {
        return userService.getJoinedConcertList(profileName)
    }

    @GetMapping("/profile-name/{profile_name}/joined-concert/poster/")
    @ResponseStatus(HttpStatus.OK)
    fun getJoinedConcertListGroupByConcertId(
        @PathVariable("profile_name") profileName: String,
    ): ConcertParticipantDto.ParticipatedConcertsGroupByConcertIdListResponse {
        return userService.getJoinedConcertListGroupByConcertId(profileName)
    }

    @PostMapping("/nickname/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun checkDuplicatedNickname(
        @Valid @RequestBody
        nicknameRequest: UserDto.NicknameRequest,
    ) {
        userService.checkDuplicatedNickname(nicknameRequest)
    }

    @GetMapping("/profile-name/{profile_name}/")
    @ResponseStatus(HttpStatus.OK)
    fun getUserInformation(
        @PathVariable("profile_name") profileName: String,
        @CurrentUser currentUser: User?,
    ): UserDto.DetailUserInfoResponse {
        return userService.getUserInformation(profileName, currentUser)
    }

    @GetMapping("/profile-name/{profile_name}/friends/")
    @ResponseStatus(HttpStatus.OK)
    fun getUserFriendList(
        @PathVariable("profile_name") profileName: String,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
    ): PageResponse<FriendDto.FriendResponse> {
        return friendService.getFriendList(profileName, pageable)
    }

    @GetMapping("/profile-name/{profile_name}/guestbook/")
    @ResponseStatus(HttpStatus.OK)
    fun getUserGuestBookList(
        @PathVariable("profile_name") profileName: String,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC,
        ) pageable: Pageable,
        @CurrentUser currentUser: User?,
    ): PageResponse<GuestBookDto.GuestBookResponse> {
        return guestBookService.listGuestBook(profileName, currentUser, pageable)
    }

    @PutMapping("/info/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateUserInformation(
        @CurrentUser user: User,
        @Valid @RequestBody
        userInfoRequest: UserDto.UpdateUserInfoRequest,
    ) {
        userService.updateUserInformation(user, userInfoRequest)
    }

    @PutMapping("/image/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateUserProfileImage(
        @CurrentUser user: User,
        @Valid @RequestBody
        imageRequest: UserDto.ProfileImageRequest,
    ) {
        userService.updateProfileImage(user, imageRequest)
    }

    @PutMapping("/interest-music/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateUserProfileImage(
        @CurrentUser user: User,
        @Valid @RequestBody
        interestMusicIds: UserDto.InterestMusicIdListRequest,
    ) {
        userService.updateUserInterestMusicList(user, interestMusicIds)
    }

    @GetMapping("/pofile-name/{profile_name}/interest-music/")
    @ResponseStatus(HttpStatus.OK)
    fun getUserProfileImage(
        @PathVariable("profile_name") profileName: String,
    ): MusicDto.MusicDetailListResponse {
        return userService.getUserInterestMusicList(profileName)
    }
}
