package com.umjari.server.domain.album.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotBlank

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["owner_id", "title"]),
    ],
)
class Album(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    val owner: User,

    @field:NotBlank
    var title: String,

    @OneToMany(mappedBy = "album", cascade = [CascadeType.REMOVE])
    @OrderBy("createdAt ASC")
    var photos: MutableList<Photo> = mutableListOf(),
) : BaseTimeEntity()
