package com.umjari.server.domain.auth.service

import com.umjari.server.domain.auth.dto.AuthDto
import com.umjari.server.domain.auth.exception.EmailNotVerifiedException
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.user.exception.DuplicatedUserEmailException
import com.umjari.server.domain.user.exception.DuplicatedUserIdException
import com.umjari.server.domain.user.exception.DuplicatedUserNicknameException
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val verifyTokenRepository: VerifyTokenRepository,
) {
    @Transactional
    fun signUp(signUpRequest: AuthDto.SignUpRequest): User {
        val verificationToken = verifyTokenRepository.findTopByEmailOrderByCreatedAtDesc(signUpRequest.email!!)
            ?: throw EmailNotVerifiedException()
        if (!verificationToken.confirmed || ChronoUnit.SECONDS.between(
                verificationToken.updatedAt!!,
                LocalDateTime.now(),
            ) > 600
        ) {
            throw EmailNotVerifiedException()
        }

        if (userRepository.existsByUserId(signUpRequest.userId!!)) {
            throw DuplicatedUserIdException(signUpRequest.userId)
        }
        if (userRepository.existsByNickname(signUpRequest.nickname!!)) {
            throw DuplicatedUserNicknameException(signUpRequest.nickname)
        }
        if (userRepository.existsByEmail(signUpRequest.email)) {
            throw DuplicatedUserEmailException(signUpRequest.email)
        }
        val encodedPassword = passwordEncoder.encode(signUpRequest.password)
        val user = User(
            userId = signUpRequest.userId,
            password = encodedPassword,
            name = signUpRequest.name!!,
            email = signUpRequest.email,
            intro = signUpRequest.intro,
            nickname = signUpRequest.nickname,
        )

        val userObject = userRepository.save(user)
        verifyTokenRepository.deleteAllByEmail(signUpRequest.email)
        return userObject
    }
}
