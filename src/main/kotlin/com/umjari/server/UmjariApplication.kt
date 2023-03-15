package com.umjari.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UmjariApplication

fun main(args: Array<String>) {
	runApplication<UmjariApplication>(*args)
}
