package com.umjari.server.domain.user.model

import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.region.model.Region
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", referencedColumnName = "id", nullable = true)
    var region: Region? = null,

    var roles: String = "ROLE_USER",

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    @OrderBy("id ASC")
    var career: MutableList<GroupMember> = mutableListOf(),

    @field:NotNull
    @field:Pattern(regexp = "^,(,|(\\d+,){1,11})$")
    var interestMusics: String,
) : BaseTimeEntity() {
    fun getInterestMusicIdList() = interestMusics.split(",").filter { it.isNotEmpty() }.map { it.toLong() }
}
