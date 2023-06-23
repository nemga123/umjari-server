package com.umjari.server.domain.group.model

import com.umjari.server.domain.concert.model.Concert
import com.umjari.server.domain.region.model.Region
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

@Entity
@Table(name = "umjari_group")
class Group(
    @field:NotEmpty
    var name: String,

    @field:NotEmpty
    var logo: String = "default_image",

    @field:NotEmpty
    var practiceTime: String,

    @field:NotNull
    var audition: Boolean,

    @field:NotNull
    @field:PositiveOrZero
    var membershipFee: Int,

    @field:NotNull
    @field:PositiveOrZero
    var monthlyFee: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    var region: Region,

    @field:NotNull
    var regionDetail: String,

    var homepage: String?,

    var detailIntro: String?,

    @field:NotNull
    var recruit: Boolean = false,

    @ElementCollection
    var recruitInstruments: MutableList<Instrument> = mutableListOf(),

    @Column(columnDefinition = "TEXT")
    var recruitDetail: String = "",

    @OneToMany(mappedBy = "group")
    var concerts: MutableList<Concert> = mutableListOf(),
) : BaseEntity()
