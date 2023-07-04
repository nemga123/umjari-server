package com.umjari.server.global.mail

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component
class MailSender(
    private val emailSender: JavaMailSender,
) {
    fun sendMail(receiver: String, subject: String, content: String) {
        val mail = convertToMail(receiver, subject, content)
        emailSender.send(mail)
    }

    private fun convertToMail(receiver: String, subject: String, content: String): MimeMessage {
        val message = emailSender.createMimeMessage()
        message.setFrom(InternetAddress("umjari.register@gmail.com", "umjari.register"))
        message.setText(content, "utf-8", "html")

        val helper = MimeMessageHelper(message)
        helper.setTo(receiver)
        helper.setSubject(subject)

        return message
    }
}
