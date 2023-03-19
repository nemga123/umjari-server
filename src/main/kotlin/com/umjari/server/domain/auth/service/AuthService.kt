package com.umjari.server.domain.auth.service

import com.umjari.server.domain.auth.dto.AuthDto
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun signUp(signUpRequest: AuthDto.SignUpRequest): User {
        // TODO: Uniqueness check
        val encodedPassword = passwordEncoder.encode(signUpRequest.password)
        val user = User(
            userId = signUpRequest.userId,
            password = encodedPassword,
            email = signUpRequest.email,
            intro = signUpRequest.intro,
            phoneNumber = signUpRequest.phoneNumber,
            nickname = signUpRequest.nickname,
        )
        return userRepository.save(user)
    }
}
