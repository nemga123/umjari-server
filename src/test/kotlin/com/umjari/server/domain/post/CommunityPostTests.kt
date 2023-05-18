package com.umjari.server.domain.post

import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.post.repository.CommunityPostReplyRepository
import com.umjari.server.domain.post.repository.CommunityPostRepository
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CommunityPostTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        private lateinit var userToken1: String
        private lateinit var userToken2: String

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
    fun testCreatePost() {
        val anonymousPost = """
            {
              "title": "TITLE",
              "content": "CONTENT",
              "isAnonymous": true
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/board/VIOLIN/post/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(anonymousPost)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isCreated,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/board/NOT_INSTRUMENT/post/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(anonymousPost)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNotFound,
        )

        val notAnonymousPost = """
            {
              "title": "TITLE",
              "content": "CONTENT",
              "isAnonymous": false
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/board/VIOLIN/post/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notAnonymousPost)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isCreated,
        )
    }

    @Test
    @Order(2)
    fun testUpdatePost() {
        val anonymousPost = """
            {
              "board": "VIOLIN",
              "title": "NEW_TITLE",
              "content": "NEW_CONTENT",
              "isAnonymous": true
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/board/VIOLIN/post/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(anonymousPost)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/board/VIOLIN/post/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(anonymousPost)
                .header("Authorization", userToken2),
        ).andExpect(
            status().isForbidden,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/board/VIOLIN/post/100/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(anonymousPost)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(3)
    fun testCreateReply() {
        val anonymousReply = """
            {
              "content": "CONTENT",
              "isAnonymous": true
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/board/VIOLIN/post/1/reply/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(anonymousReply)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isCreated,
        )

        val notAnonymousReply = """
            {
              "content": "CONTENT",
              "isAnonymous": false
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/board/VIOLIN/post/1/reply/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notAnonymousReply)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isCreated,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/board/VIOLIN/post/100/reply/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notAnonymousReply)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(4)
    fun testRetrievePost() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/board/VIOLIN/post/1/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.nickname").exists(),
        ).andExpect(
            jsonPath("$.replies.length()").value(2),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/board/VIOLIN/post/2/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.authorInfo").exists(),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/board/VIOLIN/post/100/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(5)
    fun testUpdateReply() {
        val updateReply = """
            {
              "content": "NEW_CONTENT",
              "isAnonymous": true
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/board/VIOLIN/post/1/reply/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateReply)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/board/VIOLIN/post/1/reply/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateReply)
                .header("Authorization", userToken2),
        ).andExpect(
            status().isForbidden,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/board/VIOLIN/post/1/reply/100/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateReply)
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(6)
    fun testDeleteReply(
        @Autowired communityPostReplyRepository: CommunityPostReplyRepository,
    ) {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/board/VIOLIN/post/1/reply/100/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNotFound,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/board/VIOLIN/post/1/reply/1/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNoContent,
        )
        assert(communityPostReplyRepository.findByIdOrNull(1)!!.isDeleted)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/board/VIOLIN/post/1/reply/1/")
                .header("Authorization", userToken2),
        ).andExpect(
            status().isForbidden,
        )
    }

    @Test
    @Order(7)
    fun testDeletePost(
        @Autowired communityPostRepository: CommunityPostRepository,
    ) {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/board/VIOLIN/post/100/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNotFound,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/board/VIOLIN/post/1/")
                .header("Authorization", userToken2),
        ).andExpect(
            status().isForbidden,
        )

        assert(communityPostRepository.findByIdOrNull(1) != null)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/board/VIOLIN/post/1/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isNoContent,
        )

        assert(communityPostRepository.findByIdOrNull(1) == null)
    }
}
