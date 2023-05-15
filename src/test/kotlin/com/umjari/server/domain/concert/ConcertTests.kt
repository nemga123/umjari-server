package com.umjari.server.domain.concert

import com.umjari.server.domain.concert.repository.ConcertMusicRepository
import com.umjari.server.domain.concert.repository.ConcertRepository
import com.umjari.server.domain.group.model.GroupMember
import com.umjari.server.domain.group.repository.GroupMemberRepository
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.music.model.Music
import com.umjari.server.domain.music.repository.MusicRepository
import com.umjari.server.domain.region.repository.RegionRepository
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
              "host": "HOST",
              "support": "SUPPORT",
              "qna": "QNA",
              "concertInfo": "INFO",
              "posterImg": "IMG",
              "concertDate": "2023-01-01 86:47:35",
              "concertRunningTime": 100,
              "fee": 0,
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

        musicRepository.save(Music(composerEng = "Composer", composerKor = "작곡가", nameKor = "노래", nameEng = "music"))
        val contentWithMusic = """
            {
              "title": "TITLE",
              "subtitle": "SUBTITLE",
              "conductor": "CONDUCTOR",
              "host": "HOST",
              "support": "SUPPORT",
              "qna": "QNA",
              "concertInfo": "INFO",
              "posterImg": "IMG",
              "concertDate": "2023-01-01 86:47:35",
              "concertRunningTime": 100,
              "fee": 0,
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
              "host": "HOST",
              "support": "SUPPORT",
              "qna": "QNA",
              "posterImg": "IMG",
              "concertDate": "2023-01-01 86:47:35",
              "concertRunningTime": 100,
              "fee": 0,
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
              "userIds": ["user"]
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
              "userIds": ["user"]
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
            jsonPath("$.failedUsers.length()").value(1),
        )

        val notUserContent = """
            {
              "userIds": ["NOT_USER"]
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
    fun testRemoveConcertParticipants(
        @Autowired concertMusicRepository: ConcertMusicRepository,
    ) {
        val content = """
            {
              "userIds": ["user"]
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/concert/1/concert-music/1/participant/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .header("Authorization", userToken),
        ).andExpect(
            status().isNoContent,
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
    @Order(7)
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
    @Order(7)
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
                .param("text", "NEW"),
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/concert/dashboard/")
                .param("startDate", "2023-01-01")
                .param("endDate", "2050-12-31")
                .param("regionParent", "서울시")
                .param("regionChild", "강남구")
                .param("text", "NEW"),
        ).andExpect(
            jsonPath("$.contents.length()").value(0),
        )
    }
}
