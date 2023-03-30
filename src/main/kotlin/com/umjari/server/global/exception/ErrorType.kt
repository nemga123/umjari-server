package com.umjari.server.global.exception

enum class ErrorType(
    val code: Int,
) {
    INVALID_REQUEST(0),

    NOT_ALLOWED(3000),

    DATA_NOT_FOUND(4000),
    GROUP_ID_NOT_FOUND(4001),
    CONCERT_ID_NOT_FOUND(4002),

    CONFLICT(9000),

    SERVER_ERROR(10000),
}
