package com.umjari.server.domain.mypage

import com.umjari.server.domain.group.group.repository.GroupRepository
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.region.repository.RegionRepository
import com.umjari.server.domain.user.repository.UserRepository
import com.umjari.server.utils.TestUtils
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class MyPageTests {
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
            @Autowired regionRepository: RegionRepository,
            @Autowired groupRepository: GroupRepository,
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

            TestUtils.createDummyGroup(regionRepository, groupRepository)

            createPost(mockMvc, userToken1)
            createPost(mockMvc, userToken1)
            createPost(mockMvc, userToken2)
            createPost(mockMvc, userToken3)
            likePost(mockMvc, userToken1, 3)
            likePost(mockMvc, userToken2, 1)
            commentPost(mockMvc, userToken1, 4)
            commentPost(mockMvc, userToken1, 4)
            commentPost(mockMvc, userToken1, 2)
            createGroupQna(mockMvc, userToken1)
            createGroupQna(mockMvc, userToken1)
        }

        private fun commentPost(mockMvc: MockMvc, userToken: String, id: Long) {
            val content = """
                {
                  "content": "CONTENT",
                  "isAnonymous": true
                }
            """.trimIndent()

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/board/VIOLIN/post/$id/reply/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content)
                    .header("Authorization", userToken),
            ).andExpect(
                status().isCreated,
            )
        }

        private fun likePost(mockMvc: MockMvc, userToken: String, id: Long) {
            mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/board/VIOLIN/post/$id/likes/")
                    .header("Authorization", userToken),
            ).andExpect(
                status().isNoContent,
            )
        }

        private fun createGroupQna(mockMvc: MockMvc, userToken: String) {
            val content = """
                {
                  "title": "QNA_TITLE1",
                  "content": "QNA_CONTENT1",
                  "isAnonymous": false
                }
            """.trimIndent()
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/group/1/qna/")
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", userToken),
            ).andExpect(
                status().isCreated,
            )
        }

        private fun createPost(mockMvc: MockMvc, userToken: String) {
            val content = """
                {
                  "title": "TITLE",
                  "content": "CONTENT",
                  "isAnonymous": true
                }
            """.trimIndent()
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/board/VIOLIN/post/")
                    .content(content)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", userToken),
            ).andExpect(
                status().isCreated,
            )
        }
    }

    @Test
    fun testGetMyPostList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/me/posts/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(2),
        ).andExpect(
            jsonPath("$.contents[1].likeCount").value(1),
        )
    }

    @Test
    fun testGetLikedPostList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/me/liked-posts/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        ).andExpect(
            jsonPath("$.contents[0].id").value(3),
        )
    }

    @Test
    fun testGetRepliedPostList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/me/replied-posts/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(2),
        ).andExpect(
            jsonPath("$.contents[0].id").value(4),
        )
    }

    @Test
    fun testGetReplyList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/me/replies/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(3),
        ).andExpect(
            jsonPath("$.contents[0].post.id").value(2),
        )
    }

    @Test
    fun testGetQnaList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/me/qna/")
                .header("Authorization", userToken1),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(2),
        ).andExpect(
            jsonPath("$.contents[0].id").value(2),
        )
    }
}
