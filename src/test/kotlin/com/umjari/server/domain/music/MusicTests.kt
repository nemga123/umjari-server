package com.umjari.server.domain.music

import com.umjari.server.domain.group.group.repository.GroupRepository
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.group.members.repository.GroupMemberRepository
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
class MusicTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        private lateinit var userToken: String

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
            val userResult = TestUtils.createDummyUser(mockMvc, userRepository, verifyTokenRepository)
            val user = userResult.first
            userToken = userResult.second
            groupMemberRepository.save(GroupMember(group, user, GroupMember.MemberRole.ADMIN))
        }
    }

    @Test
    @Order(1)
    fun testRegisterNewMusic() {
        val content = """
            {
              "composerEng": "COMPOSER",
              "shortComposerEng": "COM",
              "composerKor": "작곡가",
              "shortComposerKor": "작.곡",
              "nameEng": "song",
              "shortNameEng": "so",
              "nameKor": "노래",
              "shortNameKor": "놀"
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/music/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isCreated,
        ).andExpect(
            jsonPath("$.id").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/music/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isCreated,
        ).andExpect(
            jsonPath("$.id").value(1),
        )
    }

    @Test
    @Order(2)
    fun testGetMusicList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/music/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.counts").value(1),
        )
    }
}
