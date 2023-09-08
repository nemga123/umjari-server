package com.umjari.server.domain.auth.service

import com.umjari.server.domain.auth.component.PasswordResetMailSender
import com.umjari.server.domain.auth.component.UserIdMailSender
import com.umjari.server.domain.auth.dto.AuthDto
import com.umjari.server.domain.auth.exception.EmailNotVerifiedException
import com.umjari.server.domain.auth.exception.ResetPasswordForbiddenException
import com.umjari.server.domain.auth.exception.UserIdMailForbiddenException
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.region.service.RegionService
import com.umjari.server.domain.user.exception.DuplicatedUserEmailException
import com.umjari.server.domain.user.exception.DuplicatedUserIdException
import com.umjari.server.domain.user.exception.DuplicatedUserNicknameException
import com.umjari.server.domain.user.exception.DuplicatedUserProfileNameException
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val verifyTokenRepository: VerifyTokenRepository,
    private val passwordResetMailSender: PasswordResetMailSender,
    private val userIdMailSender: UserIdMailSender,
    private val regionService: RegionService,
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
        if (userRepository.existsByProfileName(signUpRequest.profileName!!)) {
            throw DuplicatedUserProfileNameException(signUpRequest.profileName)
        }
        val encodedPassword = passwordEncoder.encode(signUpRequest.password)

        val region = if (!signUpRequest.regionParent.isNullOrBlank() && !signUpRequest.regionChild.isNullOrBlank()) {
            regionService.getOrCreateRegion(
                signUpRequest.regionParent,
                signUpRequest.regionChild,
            )
        } else {
            null
        }

        val user = User(
            userId = signUpRequest.userId,
            password = encodedPassword,
            profileName = signUpRequest.profileName,
            email = signUpRequest.email,
            intro = signUpRequest.intro,
            nickname = signUpRequest.nickname,
            nicknameUpdatedAt = LocalDate.now(),
            profileImage = signUpRequest.profileImage!!,
            region = region,
            interestMusics = ",,",
        )

        val userObject = userRepository.save(user)
        verifyTokenRepository.deleteAllByEmail(signUpRequest.email)
        return userObject
    }

    fun updatePassword(updatePasswordRequest: AuthDto.UpdatePasswordRequest, currentUser: User) {
        if (!passwordEncoder.matches(updatePasswordRequest.currentPassword, currentUser.password)) {
            throw ResetPasswordForbiddenException()
        }
        val encodedPassword = passwordEncoder.encode(updatePasswordRequest.newPassword)
        currentUser.password = encodedPassword
        userRepository.save(currentUser)
    }

    fun resetPassword(findPasswordRequest: AuthDto.FindPasswordRequest) {
        val currentUser = userRepository.findByUserIdAndEmail(findPasswordRequest.userId!!, findPasswordRequest.email!!)
            ?: throw ResetPasswordForbiddenException()

        val temporaryPassword = generateTemporaryPassword()
        currentUser.password = passwordEncoder.encode(temporaryPassword)
        userRepository.save(currentUser)
        val contextVariables = mapOf("password" to temporaryPassword)
        passwordResetMailSender.sendMail(findPasswordRequest.email, contextVariables)
    }

    fun sendUserIdMail(userIdMailRequest: AuthDto.UserIdMailRequest) {
        val currentUser = userRepository.findByEmail(userIdMailRequest.email!!)
            ?: throw UserIdMailForbiddenException()

        val contextVariables = mapOf("userId" to currentUser.userId)
        userIdMailSender.sendMail(userIdMailRequest.email, contextVariables)
    }

    private fun generateTemporaryPassword(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return ThreadLocalRandom.current()
            .ints(10, 0, charPool.size)
            .asSequence()
            .map(charPool::get)
            .joinToString("")
    }
}
