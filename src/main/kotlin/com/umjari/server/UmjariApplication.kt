package com.umjari.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class UmjariApplication

fun main(args: Array<String>) {
    runApplication<UmjariApplication>(*args)
}
