package com.umjari.server.domain.image.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
class Image(
    @Column(unique = true)
    @field:NotBlank
    @field:Size(max = 36)
    val token: String,

    @field:NotBlank
    val fileName: String,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    val owner: User,
) : BaseEntity()
