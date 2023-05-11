package com.umjari.server.global.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(allowedTargets = [AnnotationTarget.FIELD])
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [KeyWordsBlockValidator::class])
annotation class KeyWordsBlock(
    val message: String = "포함할 수 없는 키워드가 포함되어있습니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
