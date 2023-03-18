package com.umjari.server.domain.auth.controller

import com.umjari.server.domain.auth.JwtTokenProvider
import com.umjari.server.domain.auth.dto.AuthDto
import com.umjari.server.domain.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


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
}