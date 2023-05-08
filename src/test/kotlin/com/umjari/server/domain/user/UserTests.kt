package com.umjari.server.domain.user

import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.user.repository.UserRepository
import com.umjari.server.utils.TestUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UserTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        private lateinit var token: String

        @BeforeAll
        @JvmStatic
        internal fun init(
            @Autowired mockMvc: MockMvc,
            @Autowired userRepository: UserRepository,
            @Autowired verifyTokenRepository: VerifyTokenRepository,
        ) {
            val result = TestUtils.createDummyUser(mockMvc, userRepository, verifyTokenRepository)
            token = result.second
        }
    }

    @Test
    @Order(1)
    fun testGetMyInfo() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/me/")
                .header("Authorization", token),
        ).andExpect(
            MockMvcResultMatchers.status().isOk,
        )
    }

    @Test
    @Order(2)
    fun testNicknameCheck() {
        val duplicatedNicknameContent = """
            {
                "nickname": "user"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/user/nickname/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicatedNicknameContent)
                .header("Authorization", token),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        )

        val notDuplicatedNicknameContent = """
            {
                "nickname": "new_user"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/user/nickname/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notDuplicatedNicknameContent)
                .header("Authorization", token),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )
    }

    @Test
    @Order(2)
    fun testNicknameCheckWithWrongContent() {
        val duplicatedNicknameContent = """
            {
                "nickname": null
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/user/nickname/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicatedNicknameContent)
                .header("Authorization", token),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        ).andExpect(
            jsonPath("$.nickname").value("must not be blank"),
        )

        val notDuplicatedNicknameContent = """
            {
                "nickname": "nickname1"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/user/nickname/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notDuplicatedNicknameContent)
                .header("Authorization", token),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )
    }
}
