package com.umjari.server.domain.image.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.umjari.server.domain.image.dto.ImageDto
import com.umjari.server.domain.image.exception.ImageNotUploadedException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class S3Service(
    private val amazonS3: AmazonS3,
) {
    @Value("\${cloud.aws.s3.bucket}")
    lateinit var bucketName: String

    fun uploadFile(
        file: MultipartFile,
        userId: Long,
        fileToken: String,
        fileName: String,
    ): ImageDto.ImageUrlResponse {
        val keyName = "images/$userId/$fileName"
        val inputStream = file.inputStream
        val contentType = file.contentType
        val meta = ObjectMetadata().also {
            it.contentType = contentType
            it.contentLength = inputStream.available().toLong()
        }

        val putObjectRequest = PutObjectRequest(bucketName, keyName, inputStream, meta)
        try {
            amazonS3.putObject(putObjectRequest)
        } catch (e: Exception) {
            throw ImageNotUploadedException()
        }
        return ImageDto.ImageUrlResponse(
            url = amazonS3.getUrl(bucketName, keyName).toString(),
            token = fileToken,
        )
    }

    fun removeFile(id: Long, fileName: String) {
        val keyName = "images/$id/$fileName"
        amazonS3.deleteObject(bucketName, keyName)
    }
}
