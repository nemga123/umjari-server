package com.umjari.server.domain.auth.controller

import com.umjari.server.domain.auth.JwtTokenProvider
import com.umjari.server.domain.auth.dto.AuthDto
import com.umjari.server.domain.auth.service.AuthService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "auth", description = "유저 인 관련 APIs")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/signup/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun signup(
        @Valid @RequestBody
        signupRequest: AuthDto.SignUpRequest,
    ): ResponseEntity<Any> {
        authService.signUp(signupRequest).let { token ->
            return ResponseEntity.noContent().header("Authorization", token).build()
        }
    }

    @PutMapping("/password/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updatePassword(
        @Valid @RequestBody
        updatePasswordRequest: AuthDto.UpdatePasswordRequest,
        @CurrentUser currentUser: User,
    ) {
        authService.updatePassword(updatePasswordRequest, currentUser)
    }

    @PostMapping("/password-reset/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun resetPassword(
        @Valid @RequestBody
        findPasswordRequest: AuthDto.FindPasswordRequest,
    ) {
        authService.resetPassword(findPasswordRequest)
    }

    @PostMapping("/id-find/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun sendUserIdMail(
        @Valid @RequestBody
        userIdMailRequest: AuthDto.UserIdMailRequest,
    ) {
        authService.sendUserIdMail(userIdMailRequest)
    }
}
