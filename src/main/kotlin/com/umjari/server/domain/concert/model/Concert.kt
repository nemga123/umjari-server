package com.umjari.server.domain.concert.model

import com.umjari.server.domain.group.model.Group
import com.umjari.server.domain.region.model.Region
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.util.Date

@Entity
@Table(name = "umjari_concert")
class Concert(
    @field:NotBlank
    var title: String,

    @field:NotBlank
    var subtitle: String,

    @field:NotBlank
    var conductor: String,

    @field:NotNull
    var host: String,

    @field:NotNull
    var support: String,

    @field:NotNull
    var qna: String,

    @field:NotBlank
    var concertInfo: String,

    @field:NotBlank
    var posterImg: String,

    @field:NotNull
    var concertDate: Date,

    @field:NotNull
    @field:Positive
    var concertRunningTime: Int,

    @field:NotNull
    @field:PositiveOrZero
    var fee: Int,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    var region: Region,

    @field:NotNull
    var regionDetail: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    val group: Group,
) : BaseTimeEntity()