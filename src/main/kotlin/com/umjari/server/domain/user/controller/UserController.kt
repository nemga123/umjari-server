package com.umjari.server.domain.user.controller

import com.umjari.server.domain.user.dto.UserDto
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.service.UserService
import com.umjari.server.global.auth.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "auth", description = "유저 관련 APIs")
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/me/")
    fun getMyInfo(@CurrentUser user: User): UserDto.UserInfoResponse {
        return UserDto.UserInfoResponse(user)
    }

    @GetMapping("/my-group/")
    fun getMyGroupList(@CurrentUser user: User): UserDto.UserGroupListResponse {
        return userService.getJoinGroupList(user)
    }

    @PostMapping("/nickname/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun checkDuplicatedNickname(
        @Valid @RequestBody
        nicknameRequest: UserDto.NicknameRequest,
    ) {
        userService.checkDuplicatedNickname(nicknameRequest)
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
}
