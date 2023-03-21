package com.umjari.server.global.exception

data class ErrorResponse(
    val errorCode: Int,
    val errorMessage: String,
    val detail: String,
)
