package com.umjari.server.domain.user.model

import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

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
    var profileImage: String,

    @Column(unique = true)
    @field:NotBlank
    var profileName: String,

    @field:NotBlank
    var password: String,

    @Column(unique = true)
    @field:NotBlank
    var nickname: String,

    @field:NotNull
    var nicknameUpdatedAt: LocalDate,

    var intro: String?,

    var roles: String = "ROLE_USER",

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    @OrderBy("id ASC")
    var career: MutableList<GroupMember> = mutableListOf(),
) : BaseTimeEntity()
