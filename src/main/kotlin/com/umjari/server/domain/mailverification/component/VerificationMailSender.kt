package com.umjari.server.domain.mailverification.component

import com.umjari.server.global.mail.MailBuilder
import com.umjari.server.global.mail.MailSender
import com.umjari.server.global.mail.MailSenderInterface
import org.springframework.stereotype.Component

@Component
class VerificationMailSender(
    private val mailBuilder: MailBuilder,
    private val mailSender: MailSender,
) : MailSenderInterface {
    final override val subject: String = "[Umjari] 인증 메일"
    final override val template: String = "verifyMailTemplate"

    override fun sendMail(receiverEmail: String, contextVariables: Map<String, String>) {
        val mailContent = mailBuilder.build(contextVariables, template)
        mailSender.sendMail(receiverEmail, subject, mailContent)
    }
}
