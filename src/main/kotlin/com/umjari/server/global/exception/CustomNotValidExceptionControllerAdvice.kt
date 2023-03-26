package com.umjari.server.global.exception

import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class CustomNotValidExceptionControllerAdvice {
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun methodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val result = e.bindingResult
        val fieldErrors = result.fieldErrors
        val errorResponse = fieldErrors.associate { it.field to it.defaultMessage }
        logger.error(errorResponse.toString())
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun jsonParseError(e: HttpMessageNotReadableException): ResponseEntity<Map<String, String?>> {
        val errorResponse = mapOf("message" to e.message)
        logger.error(e.toString())
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}
