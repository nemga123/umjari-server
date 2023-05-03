package com.umjari.server.domain.mailverification.service

import com.umjari.server.domain.mailverification.dto.MailVerificationDto
import com.umjari.server.domain.mailverification.exception.InvalidTokenException
import com.umjari.server.domain.mailverification.exception.TokenAlreadyExpiredException
import com.umjari.server.domain.mailverification.model.VerifyToken
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.user.exception.DuplicatedUserEmailException
import com.umjari.server.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

@Service
class MailVerificationService(
    private val verifyTokenRepository: VerifyTokenRepository,
    private val userRepository: UserRepository,
    private val mailBuilder: MailBuilder,
    private val mailSender: MailSender,
) {
    fun verifyEmail(mailVerificationRequest: MailVerificationDto.MailVerificationRequest) {
        if (userRepository.existsByEmail(mailVerificationRequest.email!!)) {
            throw DuplicatedUserEmailException(mailVerificationRequest.email)
        }
        val verifyToken = generateVerifyToken()
        val verifyTokenObject = VerifyToken(token = verifyToken, email = mailVerificationRequest.email)
        val mailContent = mailBuilder.build(verifyToken)
        verifyTokenRepository.save(verifyTokenObject)
        mailSender.sendMail(mailVerificationRequest.email, mailContent)
    }

    fun validateToken(tokenValidationRequest: MailVerificationDto.TokenValidationRequest) {
        val verifyToken = verifyTokenRepository.findByTokenAndEmail(
            tokenValidationRequest.token!!,
            tokenValidationRequest.email!!,
        )
            ?: throw InvalidTokenException()
        val duration = ChronoUnit.SECONDS.between(LocalDateTime.now(), verifyToken.createdAt!!)
        if (duration > 600) {
            throw TokenAlreadyExpiredException()
        }
        verifyToken.confirmed = true
        verifyTokenRepository.save(verifyToken)
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
