package com.umjari.server.domain.user.model

import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

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
    var name: String,

    @field:NotBlank
    var password: String,

    @Column(unique = true)
    @field:NotBlank
    var nickname: String,

    var intro: String?,

    var roles: String = "ROLE_USER",
) : BaseTimeEntity()
