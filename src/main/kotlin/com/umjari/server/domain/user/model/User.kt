package com.umjari.server.domain.user.model

import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Entity
@Table(name = "umjari_user")
class User(
    @Column(unique = true, name = "user_id")
    @field:NotBlank
    var userId: String,

    @Column(unique = true)
    @field:Email
    @field:NotBlank
    var email: String,

    @field:NotBlank
    var password: String,

    @Column(unique = true, name = "phone_number")
    @field:Pattern(regexp = "^[0-9]{11}$")
    var phoneNumber: String,

    @Column(unique = true)
    @field:NotBlank
    var nickname: String,

    var intro: String?,
): BaseTimeEntity()