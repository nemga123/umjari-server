package com.umjari.server.domain.image.model

import com.umjari.server.domain.user.model.User
import com.umjari.server.global.model.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@EntityListeners(value = [ImageEntityListener::class])
class Image(
    @Column(unique = true)
    @field:NotBlank
    @field:Size(max = 36)
    val token: String,

    @field:NotBlank
    val fileName: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    val owner: User,
) : BaseEntity() {
    fun toUrl(): String {
        return "https://umjari-image-bucket.s3.ap-northeast-2.amazonaws.com/images/${owner.profileName}/$fileName"
    }
}
