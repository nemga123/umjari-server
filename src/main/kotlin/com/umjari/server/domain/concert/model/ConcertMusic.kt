package com.umjari.server.domain.concert.model

import com.umjari.server.domain.music.model.Music
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class ConcertMusic(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", referencedColumnName = "id")
    val concert: Concert,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", referencedColumnName = "id")
    val music: Music,

    @OneToMany(mappedBy = "concertMusic", cascade = [CascadeType.REMOVE])
    var participants: MutableList<ConcertParticipant> = mutableListOf(),
) : BaseEntity()
