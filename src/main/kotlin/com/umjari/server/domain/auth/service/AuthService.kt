package com.umjari.server.domain.auth.service

import com.umjari.server.domain.auth.dto.AuthDto
import com.umjari.server.domain.auth.model.VerifyToken
import com.umjari.server.domain.auth.repository.VerifyTokenRepository
import com.umjari.server.domain.user.exception.DuplicatedUserEmailException
import com.umjari.server.domain.user.exception.DuplicatedUserIdException
import com.umjari.server.domain.user.exception.DuplicatedUserNicknameException
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val verifyTokenRepository: VerifyTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val mailBuilder: MailBuilder,
    private val verifyMailService: VerifyMailService,
) {
    fun verifyEmail(mailVerifyRequest: AuthDto.MailVerifyRequest) {
        val verifyToken = generateVerifyToken()
        val verifyTokenObject = VerifyToken(token = verifyToken, email = mailVerifyRequest.email!!)
        val mailContent = mailBuilder.build(verifyToken)
    }

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

    private fun generateVerifyToken(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return ThreadLocalRandom.current()
            .ints(6, 0, charPool.size)
            .asSequence()
            .map(charPool::get)
            .joinToString("")
    }
}
