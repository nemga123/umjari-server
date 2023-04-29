package com.umjari.server.domain.auth.service

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class VerifyMailService(
    private val emailSender: JavaMailSender,
) {
    fun sendMail(receiver: String, content: String) {
        val mail = convertToMail(receiver, content)
        emailSender.send(mail)
    }

    private fun convertToMail(receiver: String, content: String): MimeMessage {
        val message = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)
        helper.setTo(receiver)
        helper.setSubject("[Umjari] 인증 메일")
        helper.setText(content, true)
        message.setFrom(InternetAddress("umjari.register@umjari.co.kr", "umjari.register"))

        return message
    }
}
