package com.umjari.server.domain.region.model

import com.umjari.server.domain.group.group.model.Group
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotBlank

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["parent", "child"])])
class Region(
    @Column(updatable = false)
    @field:NotBlank
    val parent: String,

    @Column(updatable = false)
    @field:NotBlank
    val child: String,

    @OneToMany(mappedBy = "region")
    var groups: MutableList<Group> = mutableListOf(),
) : BaseEntity() {
    override fun toString(): String {
        return "$parent $child"
    }
}
