package com.umjari.server.domain.concert.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["concert_music_id", "performer_id"]),
    ],
)
class ConcertParticipant(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_music_id", referencedColumnName = "id")
    val concertMusic: ConcertMusic,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performer_id", referencedColumnName = "id")
    val performer: User,

    @field:NotBlank
    var part: String,

    @field:NotBlank
    var detailPart: String,

    @field:NotNull
    var role: ParticipantRole,
) : BaseTimeEntity() {
    enum class ParticipantRole {
        MASTER, PRINCIPAL, ASSISTANT_PRINCIPAL, MEMBER,
    }
}
