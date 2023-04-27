package com.umjari.server.domain.image.service

import com.umjari.server.domain.image.dto.ImageDto
import com.umjari.server.domain.image.exception.ImagePermissionNotAuthorizedException
import com.umjari.server.domain.image.exception.ImageTokenNotFoundException
import com.umjari.server.domain.image.exception.InvalidImageFormatException
import com.umjari.server.domain.image.model.Image
import com.umjari.server.domain.image.repository.ImageRepository
import com.umjari.server.domain.user.model.User
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class ImageService(
    private val imageRepository: ImageRepository,
    private val s3Service: S3Service,
) {
    fun uploadImage(imageFile: MultipartFile, user: User): ImageDto.ImageUrlResponse {
        val fileToken = UUID.randomUUID().toString()
        val fileName = imageFile.originalFilename!!
        val extension = fileName.split(".").last()
        val shortFileName = if (fileName.length > 100) fileName.substring(0, 100) else fileName
        if (listOf("jpg", "jpeg", "png").none { it.equals(extension, ignoreCase = true) }) {
            throw InvalidImageFormatException(extension)
        }

        val image = Image(token = fileToken, fileName = "$shortFileName.$extension", owner = user)
        return s3Service.uploadFile(imageFile, user.nickname, fileToken, fileName)
            .also { imageRepository.save(image) }
    }

    fun removeImage(tokenRequest: ImageDto.ImageTokenRequest, user: User) {
        val image = imageRepository.findByToken(tokenRequest.token!!)
            ?: throw ImageTokenNotFoundException()

        if (image.owner.id != user.id) {
            throw ImagePermissionNotAuthorizedException()
        }

        s3Service.removeFile(user.userId, image.token, image.fileName)
    }
}
