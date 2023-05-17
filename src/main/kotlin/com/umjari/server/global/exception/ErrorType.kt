package com.umjari.server.global.exception

enum class ErrorType(
    val code: Int,
) {
    INVALID_REQUEST(0),
    QNA_CANNOT_BE_UPDATED(1),
    DUPLICATED_USER_ID(11),
    DUPLICATED_USER_NICKNAME(12),
    DUPLICATED_USER_EMAIL(13),
    DUPLICATED_USER_PROFILE_NAME(14),
    NOT_VERIFIED_EMAIL(14),
    INVALID_IMAGE_FORMAT(21),
    INVALID_VERIFICATION_TOKEN(31),
    TOKEN_ALREADY_EXPIRED(32),

    NOT_ALLOWED(3000),
    GROUP_ROLE_NOT_AUTHORIZED(3001),
    IMAGE_PERMISSION_NOT_AUTHORIZED(3011),
    COMMUNITY_POST_PERMISSION_NOT_AUTHORIZED(3021),

    DATA_NOT_FOUND(4000),
    GROUP_ID_NOT_FOUND(4001),
    CONCERT_ID_NOT_FOUND(4002),
    QNA_ID_NOT_FOUND(4003),
    IMAGE_TOKEN_NOT_FOUND(4004),
    USER_PROFILE_NAME_NOT_FOUND(4005),
    MUSIC_ID_NOT_FOUND(4006),
    CONCERT_MUSIC_ID_NOT_FOUND(4007),
    COMMUNITY_POST_ID_NOT_FOUND(4008),
    COMMUNITY_POST_REPLY_ID_NOT_FOUND(4009),

    CONFLICT(9000),
    IMAGE_NOT_UPLOADED(9001),

    EXTERNAL_ERROR(10000),
}
