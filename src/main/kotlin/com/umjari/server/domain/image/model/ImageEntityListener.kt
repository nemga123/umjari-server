package com.umjari.server.domain.image.model

import com.umjari.server.domain.image.service.S3Service
import jakarta.persistence.PreRemove
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ImageEntityListener {
    @Autowired
    lateinit var s3Service: S3Service

    @PreRemove
    fun preRemove(image: Image) {
        s3Service.removeFile(image.owner.userId, image.fileName)
    }
}
