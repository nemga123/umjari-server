package com.umjari.server.domain.friend

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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class FriendTests {
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
        ) {
            val userResult = TestUtils.createDummyUser(mockMvc, userRepository, verifyTokenRepository)
            userToken1 = userResult.second

            val userResult2 = TestUtils.createDummyUser(
                mockMvc,
                userRepository,
                verifyTokenRepository,
                "user3@email.com",
                "user3",
                "profileName3",
                "nickname3",
            )
            userToken2 = userResult2.second

            val userResult3 = TestUtils.createDummyUser(
                mockMvc,
                userRepository,
                verifyTokenRepository,
                "user4@email.com",
                "user4",
                "profileName4",
                "nickname4",
            )
            userToken3 = userResult3.second
        }
    }

    @Test
    @Order(1)
    fun testPostFriendRequest() {
        var content = """
            {
                "receiverId": 2
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/friend/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isCreated,
        )

        content = """
            {
                "receiverId": 3
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/friend/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isCreated,
        )

        val selfRequestContent = """
            {
                "receiverId": 1
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/friend/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(selfRequestContent)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isBadRequest,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/friend/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isBadRequest, // Already requested
        )

        val failedContent = """
            {
                "receiverId": 100
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/friend/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(failedContent)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNotFound, // User Id Not Found
        )
    }

    @Test
    @Order(2)
    fun testGetFriendRequestList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/friend/requests/")
                .header("Authorization", userToken2),
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        ).andExpect(
            jsonPath("$.contents[0].user.id").value(1),
        )
    }

    @Test
    @Order(3)
    fun testApproveFriendRequest(
        @Autowired userRepository: UserRepository,
    ) {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/friend/approval/1/")
                .header("Authorization", userToken2),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/friend/approval/100/")
                .header("Authorization", userToken2),
        ).andExpect(
            status().isNotFound,
        )

        val user2 = userRepository.findByIdOrNull(2)!!
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/${user2.profileName}/")
                .header("Authorization", userToken1),
        ).andExpect(
            jsonPath("$.isFriend").value(true),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/${user2.profileName}/"),
        ).andExpect(
            jsonPath("$.isFriend").value(false),
        )
    }

    @Test
    @Order(3)
    fun testRejectFriendRequest(
        @Autowired userRepository: UserRepository,
    ) {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/friend/rejection/2/")
                .header("Authorization", userToken3),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/friend/rejection/100/")
                .header("Authorization", userToken3),
        ).andExpect(
            status().isNotFound,
        )

        val user3 = userRepository.findByIdOrNull(3)!!
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/${user3.profileName}/")
                .header("Authorization", userToken1),
        ).andExpect(
            jsonPath("$.isFriend").value(false),
        )
    }

    @Test
    @Order(4)
    fun testGetFriendList(
        @Autowired userRepository: UserRepository,
    ) {
        val user1 = userRepository.findByIdOrNull(1)!!

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/${user1.profileName}/friends/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        ).andExpect(
            jsonPath("$.contents[0].user.id").value(2),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/NOT-EXIST-NAME/friends/"),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(5)
    fun testDeleteFriend(
        @Autowired userRepository: UserRepository,
    ) {
        val user1 = userRepository.findByIdOrNull(1)!!

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/friend/1/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/${user1.profileName}/friends/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(0),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/friend/100/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNotFound,
        )
    }
}
