package com.umjari.server.domain.album.model

import com.umjari.server.domain.image.model.Image
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne

@Entity
class Photo(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", referencedColumnName = "id")
    val album: Album,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    val image: Image,
) : BaseTimeEntity()
