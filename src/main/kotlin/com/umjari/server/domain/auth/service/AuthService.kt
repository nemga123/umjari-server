package com.umjari.server.domain.auth.service

import com.umjari.server.domain.auth.dto.AuthDto
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.mailverification.service.MailBuilder
import com.umjari.server.domain.mailverification.service.MailVerificationService
import com.umjari.server.domain.user.exception.DuplicatedUserEmailException
import com.umjari.server.domain.user.exception.DuplicatedUserIdException
import com.umjari.server.domain.user.exception.DuplicatedUserNicknameException
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
        if (userRepository.existsByUserId(signUpRequest.userId!!)) {
            throw DuplicatedUserIdException(signUpRequest.userId)
        }
        if (userRepository.existsByNickname(signUpRequest.nickname!!)) {
            throw DuplicatedUserNicknameException(signUpRequest.nickname)
        }
        if (userRepository.existsByEmail(signUpRequest.email!!)) {
            throw DuplicatedUserEmailException(signUpRequest.email)
        }
        val encodedPassword = passwordEncoder.encode(signUpRequest.password)
        val user = User(
            userId = signUpRequest.userId,
            password = encodedPassword,
            name = signUpRequest.name!!,
            email = signUpRequest.email,
            intro = signUpRequest.intro,
            phoneNumber = signUpRequest.phoneNumber!!,
            nickname = signUpRequest.nickname,
        )
        return userRepository.save(user)
    }
}
