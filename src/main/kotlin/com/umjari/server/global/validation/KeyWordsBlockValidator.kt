package com.umjari.server.global.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class KeyWordsBlockValidator : ConstraintValidator<KeyWordsBlock, String> {
    private val keyWords: Array<String> = arrayOf("umjari", "admin", "관리자")

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) {
            return true
        }

        for (keyWord in keyWords) {
            if (value.contains(keyWord, ignoreCase = true)) {
                return false
            }
        }
        return true
    }
}
