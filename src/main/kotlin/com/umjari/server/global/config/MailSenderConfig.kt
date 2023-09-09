package com.umjari.server.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailSenderConfig {
    @Bean
    fun javaMailSendAdvice(
        @Value("\${spring.mail.host}") host: String,
        @Value("\${spring.mail.port}") port: Int,
        @Value("\${spring.mail.properties.auth}") auth: String,
        @Value("\${spring.mail.properties.starttls.enable}") enableTls: String,
        @Value("\${spring.mail.username}") username: String,
        @Value("\${spring.mail.password}") password: String,
    ): JavaMailSender {
        return JavaMailSenderImpl()
            .also { mailSenderImpl ->
                mailSenderImpl.host = host
                mailSenderImpl.port = port
                mailSenderImpl.username = username
                mailSenderImpl.password = password

                mailSenderImpl.javaMailProperties.setProperty("mail.smtp.auth", auth)
                mailSenderImpl.javaMailProperties.setProperty("mail.smtp.starttls.enable", enableTls)
                mailSenderImpl.javaMailProperties.setProperty("mail.debug", "true")
            }
    }
}
