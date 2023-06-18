package com.umjari.server.domain.groupqna

import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.repository.GroupMemberRepository
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.region.repository.RegionRepository
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class GroupQnaReplyTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        private lateinit var userToken: String
        private lateinit var adminToken: String

        @BeforeAll
        @JvmStatic
        internal fun init(
            @Autowired mockMvc: MockMvc,
            @Autowired regionRepository: RegionRepository,
            @Autowired groupRepository: GroupRepository,
            @Autowired groupMemberRepository: GroupMemberRepository,
            @Autowired userRepository: UserRepository,
            @Autowired verifyTokenRepository: VerifyTokenRepository,
        ) {
            val group = TestUtils.createDummyGroup(regionRepository, groupRepository)
            val userResult = TestUtils.createDummyUser(mockMvc, userRepository, verifyTokenRepository)
            val user = userResult.first
            userToken = userResult.second
            groupMemberRepository.save(GroupMember(group, user, GroupMember.MemberRole.ADMIN))

            val adminResult = TestUtils.createDummyAdmin(mockMvc, userRepository, verifyTokenRepository)
            adminToken = adminResult.second

            val privateQna = """
            {
              "title": "QNA_TITLE1",
              "content": "QNA_CONTENT1",
              "isAnonymous": false
            }
            """.trimIndent()

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/group/1/qna/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(privateQna)
                    .header("Authorization", userToken),
            ).andExpect(
                MockMvcResultMatchers.status().isCreated,
            )
        }
    }

    @Test
    @Order(1)
    fun testCreateGroupQnaReply() {
        val replyContent = """
            {
                "content": "REPLY_CONTENT",
                "isAnonymous": false
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/1/qna/1/reply/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(replyContent)
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )

        val anonymousReplyContent = """
            {
                "content": "REPLY_CONTENT",
                "isAnonymous": true
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/1/qna/1/reply/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(anonymousReplyContent)
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/1/qna/100/reply/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(anonymousReplyContent)
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound,
        )
    }

    @Test
    @Order(2)
    fun testUpdateGroupQnaReply() {
        val replyContent = """
            {
                "content": "NEW_CONTENT",
                "isAnonymous": false
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/qna/1/reply/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(replyContent)
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/qna/100/reply/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(replyContent)
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/qna/1/reply/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(replyContent)
                .header("Authorization", adminToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound,
        )
    }

    @Test
    @Order(3)
    fun testDeleteGroupQnaReply() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/group/1/qna/1/reply/1/")
                .header("Authorization", adminToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/group/1/qna/100/reply/1/")
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/group/1/qna/1/reply/1/")
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )
    }
}
