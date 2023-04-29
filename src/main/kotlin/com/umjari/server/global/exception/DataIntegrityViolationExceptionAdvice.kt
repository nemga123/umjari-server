package com.umjari.server.global.exception

import org.junit.jupiter.api.Order
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class DataIntegrityViolationExceptionAdvice {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    @ExceptionHandler(value = [DataIntegrityViolationException::class])
    fun methodArgumentNotValidException(e: DataIntegrityViolationException): ResponseEntity<Map<String, String?>> {
        val message = e.mostSpecificCause.message
        logger.error(message)
        return ResponseEntity(mapOf("message" to message), HttpStatus.BAD_REQUEST)
    }
}
