package com.umjari.server.domain.mailverification.service

import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class MailBuilder(
    private val templateEngine: TemplateEngine,
) {
    fun build(token: String): String {
        val context = Context()
        context.setVariable("token", token)
        return templateEngine.process("verifyMailTemplate", context)
    }
}
