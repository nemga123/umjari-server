package com.umjari.server.domain.mailverification.controller

import com.umjari.server.domain.mailverification.dto.MailVerificationDto
import com.umjari.server.domain.mailverification.service.MailVerificationService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "mail verification", description = "회원가입시 메일 인증 APIs")
@RestController
@RequestMapping("/api/v1/mail-verification")
class MailVerificationController(
    private val mailVerificationService: MailVerificationService,
) {
    @PostMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun sendVerificationMail(
        @Valid @RequestBody
        mailVerificationRequest: MailVerificationDto.MailVerificationRequest,
    ) {
        mailVerificationService.verifyEmail(mailVerificationRequest)
    }

    @PostMapping("/validate/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun validateVerificationToken(
        @Valid @RequestBody
        tokenValidationRequest: MailVerificationDto.TokenValidationRequest,
    ) {
        mailVerificationService.validateToken(tokenValidationRequest)
    }
}
