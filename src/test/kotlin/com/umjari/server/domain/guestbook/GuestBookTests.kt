package com.umjari.server.domain.guestbook

import com.umjari.server.domain.friend.model.Friend
import com.umjari.server.domain.friend.repository.FriendRepository
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class GuestBookTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        private lateinit var userToken1: String
        private lateinit var userToken2: String
        private lateinit var userToken3: String

        @BeforeAll
        @JvmStatic
        internal fun init(
            @Autowired mockMvc: MockMvc,
            @Autowired userRepository: UserRepository,
            @Autowired verifyTokenRepository: VerifyTokenRepository,
            @Autowired friendRepository: FriendRepository,
        ) {
            val userResult = TestUtils.createDummyUser(mockMvc, userRepository, verifyTokenRepository)
            userToken1 = userResult.second

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

            val userResult3 = TestUtils.createDummyUser(
                mockMvc,
                userRepository,
                verifyTokenRepository,
                "user3@email.com",
                "user3",
                "profileName3",
                "nickname3",
            )
            userToken3 = userResult3.second

            friendRepository.save(
                Friend(
                    requester = userResult.first,
                    receiver = userResult2.first,
                    status = Friend.FriendshipStatus.APPROVED
                )
            )
        }
    }

    @Test
    @Order(1)
    fun testCreateGuestBook() {
        val privateContent = """
            {
                "content": "Hello!",
                "private": true
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/guestbook/user/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(privateContent)
                .header("Authorization", userToken2)
        ).andExpect(
            status().isCreated
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/guestbook/user/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(privateContent)
                .header("Authorization", userToken3)
        ).andExpect(
            status().isForbidden
        )

        val openContent = """
            {
                "content": "Hello!",
                "private": false
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/guestbook/user/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(openContent)
                .header("Authorization", userToken2)
        ).andExpect(
            status().isCreated
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/guestbook/user/100/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(openContent)
                .header("Authorization", userToken2)
        ).andExpect(
            status().isNotFound
        )
    }

    @Test
    @Order(2)
    fun testUpdateGuestBook() {
        val updateContent = """
            {
                "content": "Hello Too Much!",
                "private": true
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/guestbook/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateContent)
                .header("Authorization", userToken2)
        ).andExpect(
            status().isNoContent
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/guestbook/100/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateContent)
                .header("Authorization", userToken2)
        ).andExpect(
            status().isNotFound
        )
    }

    @Test
    @Order(3)
    fun testListGuestBook() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/홍길동/guestbook/")
                .header("Authorization", userToken2)
        ).andExpect(
            status().isOk
        ).andExpect(
            jsonPath("$.contents.length()").value(2)
        )
    }
}
