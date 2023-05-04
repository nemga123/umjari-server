package com.umjari.server.global.auth

import com.umjari.server.domain.group.GroupTests
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
class AuthTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        private lateinit var token: String

        @BeforeAll
        @JvmStatic
        internal fun init(
            @Autowired mockMvc: MockMvc,
            @Autowired regionRepository: RegionRepository,
            @Autowired groupRepository: GroupRepository,
            @Autowired userRepository: UserRepository,
            @Autowired groupMemberRepository: GroupMemberRepository,
            @Autowired verifyTokenRepository: VerifyTokenRepository,
        ) {
            val group = TestUtils.createDummyGroup(regionRepository, groupRepository)
            val result = TestUtils.createDummyUser(mockMvc, userRepository, verifyTokenRepository)
            val user = result.first
            token = result.second
            groupMemberRepository.save(GroupMember(group, user, GroupMember.MemberRole.ADMIN))
        }
    }

    @Test
    @Order(1)
    fun testAuthenticatedApiWithoutToken() {
        val content = """
            {
              "name": "NEW_NAME",
              "practiceTime": "string",
              "audition": true,
              "membershipFee": 0,
              "monthlyFee": 0,
              "regionParent": "string",
              "regionChild": "string",
              "regionDetail": "string",
              "homepage": "string",
              "detailIntro": "string"
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(
            MockMvcResultMatchers.status().isUnauthorized,
        )
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", "JWT $token"),
        ).andExpect(
            MockMvcResultMatchers.status().isUnauthorized,
        )

    }
}
