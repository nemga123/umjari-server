package com.umjari.server.domain.album

import com.umjari.server.domain.image.model.Image
import com.umjari.server.domain.image.repository.ImageRepository
import com.umjari.server.domain.image.service.S3Service
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import com.umjari.server.utils.TestUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class AlbumTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var s3Service: S3Service

    companion object {
        private lateinit var userToken: String
        private lateinit var user: User
        private lateinit var userToken2: String
        private lateinit var user2: User

        @BeforeAll
        @JvmStatic
        internal fun init(
            @Autowired mockMvc: MockMvc,
            @Autowired userRepository: UserRepository,
            @Autowired verifyTokenRepository: VerifyTokenRepository,
        ) {
            val userResult = TestUtils.createDummyUser(mockMvc, userRepository, verifyTokenRepository)
            user = userResult.first
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
            user2 = userResult2.first
            userToken2 = userResult2.second
        }
    }

    @Test
    @Order(1)
    fun testCreateAlbum() {
        val content1 = """
            {
                "title": "ALBUM_TITLE1"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/album/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content1)
                .header("Authorization", userToken),
        ).andExpect(
            status().isCreated,
        )

        val content2 = """
            {
                "title": "ALBUM_TITLE2"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/album/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content2)
                .header("Authorization", userToken),
        ).andExpect(
            status().isCreated,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/album/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content1)
                .header("Authorization", userToken),
        ).andExpect(
            status().isBadRequest, // Duplicated title
        )
    }

    @Test
    @Order(2)
    fun testUpdateAlbumTitle() {
        val duplicatedContent = """
            {
                "title": "ALBUM_TITLE2"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/album/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicatedContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isBadRequest,
        )

        val newContent = """
            {
                "title": "NEW_ALBUM_TITLE1"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/album/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/album/100/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(3)
    fun testUploadPhotos(
        @Autowired imageRepository: ImageRepository,
    ) {
        imageRepository.saveAll(
            mutableListOf(
                Image(
                    token = "TOKEN1",
                    owner = user,
                    fileName = "TOKEN1.png",
                ),
                Image(
                    token = "TOKEN2",
                    owner = user,
                    fileName = "TOKEN2.png",
                ),
            ),
        )

        val content = """
            {
                "tokenList": ["TOKEN1","TOKEN2","TOKEN3"]
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/album/1/photo/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isCreated,
        ).andExpect(
            jsonPath("$.failedImage.length()").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/album/100/photo/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(4)
    fun testGetAlbumList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/album/profile-name/홍길동/")
                .header("Authorization", userToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.isAuthor").value(true),
        ).andExpect(
            jsonPath("$.albumPage.contents.length()").value(2),
        ).andExpect(
            jsonPath("$.albumPage.contents[1].photoCount").value(2),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/album/profile-name/홍길동/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.isAuthor").value(false),
        ).andExpect(
            jsonPath("$.albumPage.contents.length()").value(2),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/album/profile-name/홍길동/")
                .header("Authorization", userToken2),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.isAuthor").value(false),
        ).andExpect(
            jsonPath("$.albumPage.contents.length()").value(2),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/album/profile-name/NON_PROFILE_NAME/")
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(5)
    fun testGetPhotoList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/album/1/photo/")
                .header("Authorization", userToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.isAuthor").value(true),
        ).andExpect(
            jsonPath("$.photoPage.contents.length()").value(2),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/album/1/photo/")
                .header("Authorization", userToken2),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.isAuthor").value(false),
        ).andExpect(
            jsonPath("$.photoPage.contents.length()").value(2),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/album/1/photo/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.isAuthor").value(false),
        ).andExpect(
            jsonPath("$.photoPage.contents.length()").value(2),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/album/100/photo/")
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(6)
    fun testDeletePhotoList(
        @Autowired imageRepository: ImageRepository,
    ) {
        doNothing().`when`(s3Service).removeFile(anyString(), anyString())

        val content = """
            {
                "idList": [1,2]
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/album/1/photo/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        assert(imageRepository.count() == 0L)
    }

    @Test
    @Order(7)
    fun testDeleteAlbum() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/album/1/")
                .header("Authorization", userToken2),
        ).andExpect(
            status().isNotFound,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/album/1/")
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )
    }
}
