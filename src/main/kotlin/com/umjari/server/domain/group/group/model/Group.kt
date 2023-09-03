package com.umjari.server.domain.group.group.model

import com.umjari.server.domain.concert.model.Concert
import com.umjari.server.domain.group.groupmusics.model.GroupMusic
import com.umjari.server.domain.group.instruments.Instrument
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.region.model.Region
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
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

    @field:NotNull
    var tags: String,

    @OneToMany(mappedBy = "group", cascade = [CascadeType.REMOVE])
    var concerts: MutableList<Concert> = mutableListOf(),

    @OneToMany(mappedBy = "group", cascade = [CascadeType.REMOVE])
    var setList: MutableList<GroupMusic> = mutableListOf(),

    @OneToMany(mappedBy = "group", cascade = [CascadeType.REMOVE])
    var members: MutableList<GroupMember> = mutableListOf(),
) : BaseEntity() {
    fun getTagList() = tags.split(",").filter { it.isNotEmpty() }
}
