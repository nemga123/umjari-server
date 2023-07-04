package com.umjari.server.global.mail

import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class MailBuilder(
    private val templateEngine: TemplateEngine,
) {
    fun build(contextVariable: Map<String, String>, template: String): String {
        val context = Context()
        context.setVariables(contextVariable)
        return templateEngine.process(template, context)
    }
}
