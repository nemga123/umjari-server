package com.umjari.server.domain.user

import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.repository.GroupMemberRepository
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.image.service.ImageService
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.region.repository.RegionRepository
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
        private lateinit var userToken: String
        private lateinit var adminToken: String
        private lateinit var user: User

        @MockBean
        private lateinit var imageService: ImageService

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
            user = userResult.first
            userToken = userResult.second
            groupMemberRepository.save(GroupMember(group, user, GroupMember.MemberRole.ADMIN))

            val adminResult = TestUtils.createDummyAdmin(mockMvc, userRepository, verifyTokenRepository)
            adminToken = adminResult.second
        }
    }

    @Test
    @Order(1)
    fun testGetMyInfo() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/me/")
                .header("Authorization", userToken),
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
                .header("Authorization", userToken),
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
                .header("Authorization", userToken),
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
                .header("Authorization", userToken),
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
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )
    }

    @Test
    @Order(3)
    fun testGetJoinGroupList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/my-group/")
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isOk,
        ).andExpect(
            jsonPath("$.career.length()").value(1),
        )
    }

    @Test
    @Order(4)
    fun testUpdateUserProfileImage() {
        doNothing().`when`(imageService).removeImageByUrl(anyString())

        val image = """
            {
                "image": "https://default-image.png"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/user/image/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(image)
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )

        val defaultImage = """
            {
                "image": "default-image"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/user/image/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(defaultImage)
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )
    }

    @Test
    @Order(5)
    fun testUpdateUserInfo() {
        val userInfoRequest = """
                {
                    "profileName":"홍길동",
                    "nickname": "user",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/user/info/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userInfoRequest)
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )
    }

    @Test
    @Order(5)
    fun testUpdateUserInfoWithDuplicatedDate() {
        val duplicatedNicknameRequest = """
                {
                    "profileName":"홍길동",
                    "nickname": "user2",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/user/info/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicatedNicknameRequest)
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        )

        val duplicatedProfileNameRequest = """
                {
                    "profileName":"user2",
                    "nickname": "user",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/user/info/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicatedProfileNameRequest)
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        )
    }

    @Test
    @Order(6)
    fun testGetUserInformation() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/홍길동/"),
        ).andExpect(
            MockMvcResultMatchers.status().isOk,
        ).andExpect(
            jsonPath("$.id").value(1),
        ).andExpect(
            jsonPath("$.isSelfProfile").value(false),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/홍길동/")
                .header("Authorization", adminToken),
        ).andExpect(
            MockMvcResultMatchers.status().isOk,
        ).andExpect(
            jsonPath("$.id").value(1),
        ).andExpect(
            jsonPath("$.isSelfProfile").value(false),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/홍길동/")
                .header("Authorization", userToken),
        ).andExpect(
            MockMvcResultMatchers.status().isOk,
        ).andExpect(
            jsonPath("$.id").value(1),
        ).andExpect(
            jsonPath("$.isSelfProfile").value(true),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/user/profile-name/없는이름/"),
        ).andExpect(
            MockMvcResultMatchers.status().isNotFound,
        )
    }
}
