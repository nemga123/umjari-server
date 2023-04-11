package com.umjari.server.global.exception

enum class ErrorType(
    val code: Int,
) {
    INVALID_REQUEST(0),
    QNA_CANNOT_BE_UPDATED(1),

    NOT_ALLOWED(3000),
    GROUP_ROLE_NOT_AUTHORIZED(3001),

    DATA_NOT_FOUND(4000),
    GROUP_ID_NOT_FOUND(4001),
    CONCERT_ID_NOT_FOUND(4002),
    QNA_ID_NOT_FOUND(4003),

    CONFLICT(9000),

    SERVER_ERROR(10000),
}
