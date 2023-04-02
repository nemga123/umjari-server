package com.umjari.server.domain.concert

import com.umjari.server.domain.concert.repository.ConcertRepository
import com.umjari.server.domain.group.model.Group
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.region.model.Region
import com.umjari.server.domain.region.repository.RegionRepository
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
class ConcertTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var concertRepository: ConcertRepository

    companion object {
        @BeforeAll
        @JvmStatic
        internal fun init(@Autowired regionRepository: RegionRepository, @Autowired groupRepository: GroupRepository) {
            val region = Region(parent = "서울시", child = "관악구")
            regionRepository.save(region)
            val group = Group(
                name = "GROUP_NAME1",
                logo = "GROUP_LOGO",
                practiceTime = "12:00",
                audition = true,
                membershipFee = 0,
                monthlyFee = 0,
                region = region,
                regionDetail = "음대",
                detailIntro = "음악 동아리",
                homepage = "homepage",
            )
            groupRepository.save(group)
        }
    }

    @Test
    @Order(1)
    fun testCreateConcert() {
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
                .content(content),
        ).andExpect(
            status().isCreated,
        )
        Assertions.assertEquals(1, concertRepository.count())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/concert/group/100/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content),
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
                .content(content),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/100/info/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content),
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
                .content(content),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/concert/100/details/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(4)
    fun testGetConcertListByGroupId() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/1/concerts/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.contents.length()").value(1),
        )
    }
}