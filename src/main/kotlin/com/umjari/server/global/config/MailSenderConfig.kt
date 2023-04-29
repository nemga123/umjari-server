package com.umjari.server.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailSenderConfig {
    @Bean
    fun mailSender(): JavaMailSender {
        val mailSenderImpl = JavaMailSenderImpl()
        mailSenderImpl.host = ""
    }
}
