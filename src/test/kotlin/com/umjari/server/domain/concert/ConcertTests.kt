package com.umjari.server.domain.concert

import com.umjari.server.domain.concert.repository.ConcertMusicRepository
import com.umjari.server.domain.concert.repository.ConcertRepository
import com.umjari.server.domain.friend.model.Friend
import com.umjari.server.domain.friend.repository.FriendRepository
import com.umjari.server.domain.group.group.repository.GroupRepository
import com.umjari.server.domain.group.members.model.GroupMember
import com.umjari.server.domain.group.members.repository.GroupMemberRepository
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.music.model.Music
import com.umjari.server.domain.music.repository.MusicRepository
import com.umjari.server.domain.region.repository.RegionRepository
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import com.umjari.server.utils.TestUtils
import org.junit.jupiter.api.Assertions
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ConcertTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var concertRepository: ConcertRepository

    companion object {
        private lateinit var userToken: String
        private lateinit var user: User
        private lateinit var adminToken: String
        private lateinit var userToken3: String
        private lateinit var user3: User
        private lateinit var userToken4: String

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
            user3 = userResult3.first

            val userResult4 = TestUtils.createDummyUser(
                mockMvc,
                userRepository,
                verifyTokenRepository,
                "user4@email.com",
                "user4",
                "profileName4",
                "nickname4",
            )
            groupMemberRepository.save(GroupMember(group, userResult4.first, GroupMember.MemberRole.MEMBER))
            userToken4 = userResult4.second
        }
    }

    @Test
    @Order(1)
    fun testCreateConcert(
        @Autowired musicRepository: MusicRepository,
    ) {
        val content = """
            {
              "title": "TITLE",
              "subtitle": "SUBTITLE",
              "conductor": "CONDUCTOR",
              "solist": "SOLIST",
              "host": "HOST",
              "support": "SUPPORT",
              "qna": "QNA",
              "concertInfo": "INFO",
              "posterImg": "IMG",
              "concertDate": "2023-01-01 86:47:35",
              "concertRunningTime": "100분",
              "fee": "0 won",
              "link": "LINK",
              "regionParent": "서울시",
              "regionChild": "관악구",
              "regionDetail": "음대"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/concert/group/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isCreated,
        ).andExpect(
            jsonPath("$.setList.length()").value(0),
        )
        Assertions.assertEquals(1, concertRepository.count())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/concert/group/100/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )

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
        val contentWithMusic = """
            {
              "title": "TITLE",
              "subtitle": "SUBTITLE",
              "conductor": "CONDUCTOR",
              "solist": "SOLIST",
              "host": "HOST",
              "support": "SUPPORT",
              "qna": "QNA",
              "concertInfo": "INFO",
              "posterImg": "IMG",
              "concertDate": "2023-01-01 86:47:35",
              "concertRunningTime": "100분",
              "fee": "0 won",
              "link": "LINK",
              "regionParent": "서울시",
              "regionChild": "관악구",
             "regionDetail": "음대",
              "musicIds": [1, 122]
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/concert/group/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentWithMusic)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(2)
    fun testGetConcert() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/1/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.title").value("TITLE"),
        ).andExpect(
            jsonPath("$.setList.length()").value(0),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/100/"),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(3)
    fun testUpdateConcertInfo() {
        val content = """
            {
              "concertInfo": "NEW_INFO"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/info/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/100/info/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(4)
    fun testUpdateConcertDetails() {
        val content = """
            {
              "title": "NEW_TITLE",
              "subtitle": "NEW_SUBTITLE",
              "conductor": "CONDUCTOR",
              "solist": "",
              "host": "HOST",
              "support": "SUPPORT",
              "qna": "QNA",
              "posterImg": "IMG",
              "concertDate": "2023-01-01 86:47:35",
              "concertRunningTime": "100분",
              "fee": "0 won",
              "link": "LINK",
              "regionParent": "서울시",
              "regionChild": "관악구",
              "regionDetail": "음대"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/details/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/details/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", adminToken),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/details/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken3),
        ).andExpect(
            status().isForbidden,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/details/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken4),
        ).andExpect(
            status().isForbidden,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/100/details/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(4)
    fun testUpdateConcertSetList(
        @Autowired concertMusicRepository: ConcertMusicRepository,
    ) {
        val content = """
            {
              "musicIds": [1]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/set-list/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )
        assert(concertMusicRepository.existsByConcertIdAndMusicId(1, 1))

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/set-list/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )
        assert(concertMusicRepository.existsByConcertIdAndMusicId(1, 1))

        val invalidContent = """
            {
              "musicIds": [100]
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/set-list/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/100/set-list/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(5)
    fun testRegisterConcertParticipants(
        @Autowired concertMusicRepository: ConcertMusicRepository,
    ) {
        val content = """
            {
              "participantList": [
                {
                  "userId": "user",
                  "part": "PART",
                  "detailPart": "DETAIL_PART",
                  "role": "MEMBER"
                }
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/concert-music/1/participant/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.failedUsers.length()").value(0),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/concert-music/100/participant/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )

        val alreadyEnrolledContent = """
            {
              "participantList": [
                {
                  "userId": "user",
                  "part": "NEW_PART",
                  "detailPart": "DETAIL_PART",
                  "role": "MEMBER"
                }
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/concert-music/1/participant/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(alreadyEnrolledContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.failedUsers.length()").value(0),
        )

        val notUserContent = """
            {
              "participantList": [
                {
                  "userId": "NOT_USER",
                  "part": "NEW_PART",
                  "detailPart": "DETAIL_PART",
                  "role": "MEMBER"
                }
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/concert-music/1/participant/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(notUserContent)
                .header("Authorization", userToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.failedUsers.length()").value(1),
        )
    }

    @Test
    @Order(6)
    fun testGetConcertParticipantsList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/1/participant/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.participants.length()").value(1),
        ).andExpect(
            jsonPath("$.participants[0].member.length()").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/100/participant/"),
        ).andExpect(
            status().isNotFound,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/1/concert-music/1/participant/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.participants.length()").value(1),
        ).andExpect(
            jsonPath("$.participants[0].member.length()").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/1/concert-music/100/participant/"),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(7)
    fun testRemoveConcertParticipants(
        @Autowired concertMusicRepository: ConcertMusicRepository,
    ) {
        val content = """
            {
              "userIds": ["user", "NOT_ENROLLED_USER"]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/concert/1/concert-music/1/participant/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.failedUsers[0].userId").value("NOT_ENROLLED_USER"),
        ).andExpect(
            jsonPath("$.failedUsers[0].reason").value("User does not enrolled in concert."),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/concert/1/concert-music/100/participant/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(8)
    fun testGetConcertListByGroupId() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/1/concerts/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/100/concerts/"),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(8)
    fun testGetConcertDashboard() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/dashboard/"),
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/dashboard/")
                .param("startDate", "2023-01-01")
                .param("endDate", "2050-12-31")
                .param("regionParent", "전체")
                .param("regionChild", "전체")
                .param("text", "NEW")
                .param("composer", "c"),
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/dashboard/")
                .param("startDate", "2023-01-01")
                .param("endDate", "2050-12-31")
                .param("regionParent", "서울시")
                .param("regionChild", "강남구")
                .param("text", "NEW")
                .param("musicName", "NO_MUSIC"),
        ).andExpect(
            jsonPath("$.contents.length()").value(0),
        )
    }

    @Test
    @Order(8)
    fun testGetConcertDashboardWithFriendCount(
        @Autowired friendRepository: FriendRepository,
    ) {
        val content = """
            {
              "participantList": [
                {
                  "userId": "user",
                  "part": "PART",
                  "detailPart": "DETAIL_PART",
                  "role": "MEMBER"
                }
              ]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/concert-music/1/participant/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isOk,
        )

        friendRepository.save(
            Friend(
                receiver = user,
                requester = user3,
                status = Friend.FriendshipStatus.APPROVED,
            ),
        )
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/dashboard/")
                .header("Authorization", userToken3),
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        ).andExpect(
            jsonPath("$.contents[0].friendCount").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/dashboard/")
                .param("startDate", "2023-01-01")
                .param("endDate", "2050-12-31")
                .param("regionParent", "전체")
                .param("regionChild", "전체")
                .param("text", "NEW")
                .param("composer", "c"),
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/dashboard/")
                .param("startDate", "2023-01-01")
                .param("endDate", "2050-12-31")
                .param("regionParent", "서울시")
                .param("regionChild", "강남구")
                .param("text", "NEW")
                .param("musicName", "NO_MUSIC"),
        ).andExpect(
            jsonPath("$.contents.length()").value(0),
        )
    }

    @Test
    @Order(9)
    fun testDeleteConcertSetList(
        @Autowired concertMusicRepository: ConcertMusicRepository,
    ) {
        val content = """
            {
              "musicIds": []
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/1/set-list/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
        )
        assert(!concertMusicRepository.existsByConcertIdAndMusicId(1, 1))
    }
}
