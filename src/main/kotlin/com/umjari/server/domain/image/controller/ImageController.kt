package com.umjari.server.domain.image.controller

import com.umjari.server.domain.image.dto.ImageDto
import com.umjari.server.domain.image.service.ImageService
import com.umjari.server.domain.user.model.User
import com.umjari.server.global.auth.annotation.CurrentUser
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Image APIs", description = "Image API")
@RestController
@RequestMapping("/api/v1/image")
class ImageController(
    private val imageService: ImageService,
) {
    @PostMapping(value = ["/"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadImage(
        @RequestPart image: MultipartFile,
        @CurrentUser user: User,
    ): ImageDto.ImageUrlResponse {
        return imageService.uploadImage(image, user)
    }

    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.OK)
    fun removeImage(
        @Valid @RequestBody
        token: ImageDto.ImageTokenRequest,
        @CurrentUser user: User,
    ) {
        imageService.removeImage(token, user)
    }
}
