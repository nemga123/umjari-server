package com.umjari.server.domain.auth.component

import com.umjari.server.global.mail.MailBuilder
import com.umjari.server.global.mail.MailSender
import com.umjari.server.global.mail.MailSenderInterface
import org.springframework.stereotype.Component

@Component
class PasswordResetMailSender(
    private val mailBuilder: MailBuilder,
    private val mailSender: MailSender,
) : MailSenderInterface {
    final override val subject: String = "[Umjari] 임시 비밀번호 발급"
    final override val template: String = "resetPasswordMailTemplate"

    override fun sendMail(receiverEmail: String, contextVariables: Map<String, String>) {
        val mailContent = mailBuilder.build(contextVariables, template)
        mailSender.sendMail(receiverEmail, subject, mailContent)
    }
}
