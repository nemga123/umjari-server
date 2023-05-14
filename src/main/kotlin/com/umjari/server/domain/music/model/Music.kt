package com.umjari.server.domain.music.model

import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.validation.constraints.NotBlank

@Entity
class Music(
    @field:NotBlank
    @Column(updatable = false)
    val composerEng: String,

    @field:NotBlank
    @Column(updatable = false)
    val composerKor: String,

    @field:NotBlank
    @Column(updatable = false)
    val nameEng: String,

    @field:NotBlank
    @Column(updatable = false)
    val nameKor: String,
) : BaseEntity()
