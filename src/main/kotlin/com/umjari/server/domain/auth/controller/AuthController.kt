package com.umjari.server.domain.auth.controller

import com.umjari.server.domain.auth.JwtTokenProvider
import com.umjari.server.domain.auth.dto.AuthDto
import com.umjari.server.domain.auth.dto.UserDto
import com.umjari.server.domain.auth.service.AuthService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Tag(name = "auth", description = "유저 관련 APIs")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @PostMapping("/signup/")
    fun signup(@Valid @RequestBody signupRequest: AuthDto.SignUpRequest): ResponseEntity<Any> {
        val user = authService.signUp(signupRequest)
        return ResponseEntity.noContent().header("Authorization", jwtTokenProvider.generateToken(user.userId)).build()
    }

    @GetMapping("/me/")
    fun getMyInfo(@CurrentUser user: User): UserDto.UserInfoResponse {
        return UserDto.UserInfoResponse(user)
    }
}