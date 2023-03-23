package com.umjari.server.domain.user.controller

import com.umjari.server.domain.auth.dto.UserDto
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "auth", description = "유저 관련 APIs")
@RestController
@RequestMapping("/api/v1/user")
class UserController() {
    @GetMapping("/me/")
    fun getMyInfo(@CurrentUser user: User): UserDto.UserInfoResponse {
        return UserDto.UserInfoResponse(user)
    }
}