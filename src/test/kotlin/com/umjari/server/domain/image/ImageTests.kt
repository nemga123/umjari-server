package com.umjari.server.domain.image

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectResult
import com.umjari.server.domain.image.exception.ImageTokenNotFoundException
import com.umjari.server.domain.image.repository.ImageRepository
import com.umjari.server.domain.image.service.ImageService
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.user.repository.UserRepository
import com.umjari.server.utils.TestUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.FileInputStream
import java.lang.RuntimeException
import java.net.URL

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ImageTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var amazonS3: AmazonS3

    private val thumbnailPath = "src/test/resources/Thumbnail.png"
    private val inputStream = FileInputStream(thumbnailPath)
    private val mockThumbnail = MockMultipartFile("image", "Thumbnail.png", "png", inputStream)

    companion object {
        private lateinit var userToken: String
        private lateinit var userToken2: String

        @BeforeAll
        @JvmStatic
        internal fun init(
            @Autowired mockMvc: MockMvc,
            @Autowired userRepository: UserRepository,
            @Autowired verifyTokenRepository: VerifyTokenRepository,
        ) {
            val userResult = TestUtils.createDummyUser(mockMvc, userRepository, verifyTokenRepository)
            userToken = userResult.second

            val userResult2 = TestUtils.createDummyUser(
                mockMvc,
                userRepository,
                verifyTokenRepository,
                "user2@email.com",
                "user2",
                "profileName2",
                "nickname2",
            )
            userToken2 = userResult2.second
        }
    }

    @Test
    @Order(1)
    fun testUploadImage(
        @Autowired imageRepository: ImageRepository,
    ) {
        Mockito.`when`(amazonS3.putObject(any())).thenReturn(PutObjectResult())
        Mockito.`when`(amazonS3.getUrl(anyString(), anyString()))
            .thenReturn(
                URL("https://umjari-image-bucket.s3.ap-northeast-2.amazonaws.com/images/mockURL"),
            )

        mockMvc.perform(
            multipart("/api/v1/image/")
                .file(mockThumbnail)
                .header("Authorization", userToken),
        ).andExpect(
            status().isCreated,
        )

        mockMvc.perform(
            multipart("/api/v1/image/")
                .file(mockThumbnail)
                .header("Authorization", userToken2),
        ).andExpect(
            status().isCreated,
        )

        assert(imageRepository.count() == 2L)

        val inputStream = FileInputStream(thumbnailPath)
        val malformedThumbnail = MockMultipartFile("image", "Thumbnail.mp3", "mp3", inputStream)
        mockMvc.perform(
            multipart("/api/v1/image/")
                .file(malformedThumbnail)
                .header("Authorization", userToken2),
        ).andExpect(
            status().isBadRequest,
        )
    }

    @Test
    @Order(1)
    fun testExceptionRaiseWhenUploadingImage() {
        Mockito.`when`(amazonS3.putObject(any())).thenThrow(RuntimeException())

        mockMvc.perform(
            multipart("/api/v1/image/")
                .file(mockThumbnail)
                .header("Authorization", userToken),
        ).andExpect(
            status().isConflict,
        )
    }

    @Test
    @Order(2)
    fun testRemoveImage(
        @Autowired imageRepository: ImageRepository,
    ) {
        Mockito.`when`(amazonS3.putObject(any())).thenReturn(PutObjectResult())
        Mockito.`when`(amazonS3.getUrl(anyString(), anyString()))
            .thenReturn(
                URL("https://umjari-image-bucket.s3.ap-northeast-2.amazonaws.com/images/mockURL"),
            )

        mockMvc.perform(
            multipart("/api/v1/image/")
                .file(mockThumbnail)
                .header("Authorization", userToken),
        ).andExpect(
            status().isCreated,
        )

        val image = imageRepository.findByIdOrNull(3)!!

        val token = """
            {
                "token": "${image.token}"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/image/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token)
                .header("Authorization", userToken2),
        ).andExpect(
            status().isForbidden,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/image/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/image/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(token)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound, // Already deleted token
        )
    }

    @Test
    @Order(3)
    fun testRemoveImageByUrl(
        @Autowired imageRepository: ImageRepository,
        @Autowired imageService: ImageService,
    ) {
        val image = imageRepository.findById(1)!!.toUrl()
        imageService.removeImageByUrl(image)

        assert(imageRepository.findByIdOrNull(1) == null)

        assertThrows<ImageTokenNotFoundException> {
            imageService.removeImageByUrl(image)
        }
    }
}
