package com.umjari.server.domain.group

import com.umjari.server.domain.friend.model.Friend
import com.umjari.server.domain.friend.repository.FriendRepository
import com.umjari.server.domain.group.group.repository.GroupRepository
import com.umjari.server.domain.group.groupmusics.repository.GroupMusicRepository
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.group.members.repository.GroupMemberRepository
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.music.model.Music
import com.umjari.server.domain.music.repository.MusicRepository
import com.umjari.server.domain.region.repository.RegionRepository
import com.umjari.server.domain.user.model.User
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
import org.springframework.transaction.annotation.Transactional
import java.text.SimpleDateFormat

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class GroupTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    companion object {
        private lateinit var userToken: String
        private lateinit var user: User
        private lateinit var adminToken: String
        private lateinit var userToken3: String
        private lateinit var user3: User

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
            user = userResult.first
            userToken = userResult.second
            groupMemberRepository.save(GroupMember(group, user, GroupMember.MemberRole.ADMIN))

            val adminResult = TestUtils.createDummyAdmin(mockMvc, userRepository, verifyTokenRepository)
            adminToken = adminResult.second

            val userResult3 = TestUtils.createDummyUser(
                mockMvc,
                userRepository,
                verifyTokenRepository,
                "user3@email.com",
                "user3",
                "profileName3",
                "nickname3",
            )
            user3 = userResult3.first
            userToken3 = userResult3.second
        }
    }

    @Test
    @Order(1)
    fun testUpdateGroupInformation() {
        val content = """
            {
              "name": "NEW_NAME",
              "logo": "NEW_LOGO",
              "practiceTime": "string",
              "audition": true,
              "membershipFee": 0,
              "monthlyFee": 0,
              "regionParent": "string",
              "regionChild": "string",
              "regionDetail": "string",
              "homepage": "string",
              "detailIntro": "string",
              "tags": ["TAG1"]
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        val contentWithDefaultLogo = """
            {
              "name": "NEW_NAME",
              "logo": null,
              "practiceTime": "string",
              "audition": true,
              "membershipFee": 0,
              "monthlyFee": 0,
              "regionParent": "string",
              "regionChild": "string",
              "regionDetail": "string",
              "homepage": "string",
              "detailIntro": "string",
              "tags": ["TAG1"]
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentWithDefaultLogo)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/100/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )

        val newRegionContent = """
            {
              "name": "NEW_NAME",
              "practiceTime": "string",
              "audition": true,
              "membershipFee": 0,
              "monthlyFee": 0,
              "regionParent": "서울시",
              "regionChild": "관악구",
              "regionDetail": "string",
              "homepage": "string",
              "detailIntro": "string",
              "tags": ["TAG1"]
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newRegionContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )
    }

    @Test
    @Order(3)
    fun testUpdateRecruitInformation() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/is-recruit/")
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/is-recruit/")
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/100/is-recruit/")
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )

        val content = """
            {
              "recruitInstruments": [
                "VIOLIN"
              ],
              "recruitDetail": "RECRUIT_DETAIL"
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/recruit-detail/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/100/recruit-detail/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(4)
    fun testGetGroupAsMember() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/1/")
                .header("Authorization", userToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.memberType").value("ADMIN"),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/100/"),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(4)
    fun testGetGroupAsNotMember() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/1/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.memberType").value("NON_MEMBER"),
        )
    }

    @Test
    @Order(5)
    fun testGetGroupRecruitInformation() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/is-recruit/")
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/1/recruit/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.recruitDetail").value("RECRUIT_DETAIL"),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/100/recruit/"),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(6)
    fun testCreateGroupAsAdmin() {
        val content = """
            {
                "name": "GROUP_NAME2",
                "logo":"GROUP_LOGO",
                "practiceTime": "12:00",
                "audition":true,
                "membershipFee": 0,
                "monthlyFee": 0,
                "regionParent": "서울시",
                "regionChild": "관악구",
                "regionDetail": "음대",
                "detailIntro": "음악 동아리",
                "homepage": "homepage"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", adminToken),
        ).andExpect(
            status().isCreated,
        )

        val contentWithDefaultLogo = """
            {
                "name": "GROUP_NAME3",
                "logo": null,
                "practiceTime": "12:00",
                "audition":true,
                "membershipFee": 0,
                "monthlyFee": 0,
                "regionParent": "서울시",
                "regionChild": "관악구",
                "regionDetail": "음대",
                "detailIntro": "음악 동아리",
                "homepage": "homepage"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentWithDefaultLogo)
                .header("Authorization", adminToken),
        ).andExpect(
            status().isCreated,
        )
    }

    @Test
    @Order(7)
    fun testRegisterGroupAMember() {
        val content = """
            {
              "userIds": [
                "user"
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/2/register/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", adminToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.failedUsers.length()").value(0),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/100/register/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", adminToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(8)
    fun testRegisterGroupWithWrongData() {
        val noExistContent = """
            {
              "userIds": [
                "non-exist-user"
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/2/register/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(noExistContent)
                .header("Authorization", adminToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.failedUsers.length()").value(1),
        ).andExpect(
            jsonPath("$.failedUsers[0].userId").value("non-exist-user"),
        ).andExpect(
            jsonPath("$.failedUsers[0].reason").value("User does not exist."),
        )
    }

    @Test
    @Order(9)
    fun testRegisterGroupMemberAsAdmin() {
        val noExistContent = """
            {
              "userIds": [
                "user",
                "user3"
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/2/register/admin/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(noExistContent)
                .header("Authorization", adminToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.failedUsers.length()").value(0),
        )
    }

    @Test
    @Order(10)
    fun testUpdateGroupRegisterTimestamp(
        @Autowired groupMemberRepository: GroupMemberRepository,
    ) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

        val notNullContent = """
            {
                "joinedAt": "2023-01-01",
                "leavedAt": "2050-12-31"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/2/register/timestamp/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notNullContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        var groupMember = groupMemberRepository.findByGroupIdAndUserId(2, 1)!!
        assert(dateFormatter.format(groupMember.joinedAt) == "2023-01-01")
        assert(dateFormatter.format(groupMember.leavedAt) == "2050-12-31")

        val nullContent = """
            {
                "joinedAt": null,
                "leavedAt": null
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/2/register/timestamp/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nullContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        groupMember = groupMemberRepository.findByGroupIdAndUserId(2, 1)!!
        assert(groupMember.joinedAt == null)
        assert(groupMember.leavedAt == null)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/200/register/timestamp/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notNullContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isForbidden,
        )
    }

    @Test
    @Order(11)
    fun testUpdateGroupSetList(
        @Autowired groupMusicRepository: GroupMusicRepository,
        @Autowired musicRepository: MusicRepository,
    ) {
        musicRepository.save(
            Music(
                composerEng = "Composer",
                composerKor = "작곡가",
                nameKor = "노래",
                nameEng = "music",
                shortComposerEng = "c",
                shortComposerKor = "작",
                shortNameEng = "m",
                shortNameKor = "음",
            ),
        )
        val content = """
            {
              "musicIds": [1]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/set-list/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/100/set-list/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )

        val invalidContent = """
            {
              "musicIds": [1, 100]
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/set-list/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )

        val emptyContent = """
            {
              "musicIds": []
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/set-list/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        assert(groupMusicRepository.count() == 0L)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/set-list/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )
    }

    @Test
    @Order(12)
    @Transactional
    fun testSearchGroup() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/")
                .param("name", "NAME")
                .param("regionParent", "전체")
                .param("regionChild", "전체")
                .param("instruments", "")
                .param("tags", ""),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(3),
        ).andExpect(
            jsonPath("$.contents[0].friendCount").value(null),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/")
                .param("regionParent", "서울시")
                .param("regionChild", "관악구")
                .param("composer", "NO_COMPOSER")
                .param("musicName", "NO_MUSIC_NAME"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(0),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/")
                .param("composer", "c")
                .param("musicName", "노래")
                .param("tags", "tag1"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/")
                .param("instruments", "VIOLIN")
                .param("tags", "no_tag"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(0),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/?instruments=VIOLIN&instruments=TUBA")
                .param("instruments", "VIOLIN", "TUBA"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(0),
        )
    }

    @Test
    @Order(12)
    fun testSearchGroupWithFriend(
        @Autowired friendRepository: FriendRepository,
    ) {
        friendRepository.save(
            Friend(
                receiver = user,
                requester = user3,
                status = Friend.FriendshipStatus.APPROVED,
            ),
        )
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/")
                .param("name", "NAME")
                .param("regionParent", "전체")
                .param("regionChild", "전체")
                .param("instruments", "")
                .header("Authorization", userToken3),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(3),
        ).andExpect(
            jsonPath("$.contents[0].friendCount").value(1),
        )
    }

    @Test
    @Order(13)
    fun testRemoveGroupMember() {
        val content = """
            {
              "userIds": [
                "user",
                "user3"
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/group/2/register/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", adminToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.failedUsers.length()").value(0),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/group/100/register/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", adminToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(14)
    fun testRemoveWrongGroupMember() {
        val content = """
            {
              "userIds": [
                "non-user",
                "user"
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/group/2/register/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", adminToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.failedUsers.length()").value(2),
        )
    }
}
