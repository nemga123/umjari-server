package com.umjari.server.domain.groupqna

import com.umjari.server.domain.concert.ConcertTests
import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.repository.GroupMemberRepository
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.groupqna.repository.GroupQnaReplyRepository
import com.umjari.server.domain.groupqna.repository.GroupQnaRepository
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class GroupQnaTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var groupQnaRepository: GroupQnaRepository

    @Autowired
    private lateinit var groupQnaReplyRepository: GroupQnaReplyRepository

    companion object {
        private lateinit var userToken: String

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
        }
    }

    @Test
    @Order(1)
    fun testCreateGroupQna() {
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
                .header("Authorization", userToken)
        ).andExpect(
            status().isCreated
        )

        val notPrivateQna = """
            {
              "title": "QNA_TITLE2",
              "content": "QNA_CONTENT2",
              "isAnonymous": true
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/1/qna/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notPrivateQna)
                .header("Authorization", userToken)
        ).andExpect(
            status().isCreated
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/100/qna/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notPrivateQna)
                .header("Authorization", userToken)
        ).andExpect(
            status().isNotFound
        )
    }

    @Test
    @Order(2)
    fun testGetGroupQnaByGroupId() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/1/qna/")
        ).andExpect(
            status().isOk
        ).andExpect(
            jsonPath("$.contents.length()").value(2)
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/1/qna/")
                .param("text", "2")
        ).andExpect(
            status().isOk
        ).andExpect(
            jsonPath("$.contents.length()").value(1)
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/100/qna/")
        ).andExpect(
            status().isNotFound
        )
    }

    @Test
    @Order(3)
    fun testUpdateQna() {
        val updatedQna = """
            {
              "title": "NEW_QNA_TITLE",
              "content": "NEW_QNA_CONTENT",
              "isAnonymous": false
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/qna/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedQna)
                .header("Authorization", userToken)
        ).andExpect(
            status().isNoContent
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/qna/100/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedQna)
                .header("Authorization", userToken)
        ).andExpect(
            status().isNotFound
        )
    }
}
