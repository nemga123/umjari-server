package com.umjari.server.domain.group.model

import com.umjari.server.domain.region.model.Region
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

@Entity
@Table
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
    var recruit: Boolean,

    var recruitDetail: String?,
) : BaseEntity()
