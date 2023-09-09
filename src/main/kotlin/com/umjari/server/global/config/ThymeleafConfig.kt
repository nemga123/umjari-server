package com.umjari.server.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templatemode.TemplateMode

@Configuration
class ThymeleafConfig {
    @Bean
    fun templateResolver(): SpringResourceTemplateResolver {
        return SpringResourceTemplateResolver().also { resolver ->
            resolver.prefix = "classpath:templates/"
            resolver.suffix = ".html"
            resolver.templateMode = TemplateMode.HTML
            resolver.characterEncoding = "UTF-8"
            resolver.isCacheable = false
            resolver.checkExistence = true
        }
    }

    @Bean
    fun templateEngine(): SpringTemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.setTemplateResolver(templateResolver())
        templateEngine.enableSpringELCompiler = true
        return templateEngine
    }
}
