package com.umjari.server.domain.mailverification.model

import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["token", "email"])])
class VerifyToken(
    @field:NotBlank
    @field:Size(min = 6, max = 6)
    var token: String,

    @field:NotBlank
    var email: String,

    @field:NotNull
    var confirmed: Boolean = false,
) : BaseTimeEntity()
