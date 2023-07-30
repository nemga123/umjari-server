package com.umjari.server.domain.group.groupmusics.model

import com.umjari.server.domain.group.group.model.Group
import com.umjari.server.domain.music.model.Music
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["group_id", "music_id"]),
    ],
)
class GroupMusic(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    var group: Group,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", referencedColumnName = "id")
    val music: Music,
) : BaseEntity()
