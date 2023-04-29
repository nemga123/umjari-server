package com.umjari.server.domain.auth.service

import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class MailBuilder(
    private val templateEngine: TemplateEngine,
) {
    fun build(token: String): String {
        val context = Context()
        context.setVariable("token", token)
        return templateEngine.process("verifyMailTemplate", context)
    }
}
