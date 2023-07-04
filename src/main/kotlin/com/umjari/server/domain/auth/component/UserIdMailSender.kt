package com.umjari.server.domain.auth.component

import com.umjari.server.global.mail.MailBuilder
import com.umjari.server.global.mail.MailSender
import com.umjari.server.global.mail.MailSenderInterface
import org.springframework.stereotype.Component

@Component
class UserIdMailSender(
    private val mailBuilder: MailBuilder,
    private val mailSender: MailSender,
) : MailSenderInterface {
    final override val subject: String = "[Umjari] 아이디 확인"
    final override val template: String = "userIdMailTemplate"

    override fun sendMail(receiverEmail: String, contextVariables: Map<String, String>) {
        val mailContent = mailBuilder.build(contextVariables, template)
        mailSender.sendMail(receiverEmail, subject, mailContent)
    }
}
