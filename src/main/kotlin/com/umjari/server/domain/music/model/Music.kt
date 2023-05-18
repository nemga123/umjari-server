package com.umjari.server.domain.music.model

import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotBlank

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(
            columnNames = [
                "composerEng",
                "composerKor",
                "nameEng",
                "nameKor",
            ],
        ),
    ],
)
class Music(
    @field:NotBlank
    @Column(updatable = false)
    val composerEng: String,

    @field:NotBlank
    @Column(updatable = false)
    val shortComposerEng: String,

    @field:NotBlank
    @Column(updatable = false)
    val composerKor: String,

    @field:NotBlank
    @Column(updatable = false)
    val shortComposerKor: String,

    @field:NotBlank
    @Column(updatable = false)
    val nameEng: String,

    @field:NotBlank
    @Column(updatable = false)
    val shortNameEng: String,

    @field:NotBlank
    @Column(updatable = false)
    val nameKor: String,

    @field:NotBlank
    @Column(updatable = false)
    val shortNameKor: String,
) : BaseEntity()
