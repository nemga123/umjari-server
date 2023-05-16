package com.umjari.server.domain.concert.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class ConcertPerformer(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_music_id", referencedColumnName = "id")
    val concertMusic: ConcertMusic,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performer_id", referencedColumnName = "id")
    val performer: User,
) : BaseTimeEntity()
