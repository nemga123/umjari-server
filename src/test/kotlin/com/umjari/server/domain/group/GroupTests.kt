package com.umjari.server.domain.group

import com.umjari.server.domain.group.repository.GroupRepository
import org.junit.jupiter.api.Assertions
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class GroupTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Test
    @Order(1)
    fun testCreateGroup() {
        val content = """
            {
              "name": "GROUP_NAME1",
              "logo": "GROUP_LOGO",
              "practiceTime": "12:00",
              "audition": true,
              "membershipFee": 0,
              "monthlyFee": 0,
              "regionParent": "서울시",
              "regionChild": "관악구",
              "regionDetail": "음대",
              "homepage": "homepage.com",
              "detailIntro": "음악 동아리"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content),
        ).andExpect(
            status().isCreated,
        )
        Assertions.assertEquals(1, groupRepository.count())
    }

    @Test
    @Order(2)
    fun testCreateGroupWithDefaultLogo() {
        print(groupRepository.count())
        val content = """
            {
              "name": "GROUP_NAME2",
              "logo": null,
              "practiceTime": "12:00",
              "audition": true,
              "membershipFee": 0,
              "monthlyFee": 0,
              "regionParent": "서울시",
              "regionChild": "관악구",
              "regionDetail": "음대",
              "homepage": "homepage.com",
              "detailIntro": "음악 동아리"
            }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/group/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content),
        ).andExpect(
            status().isCreated,
        )
        Assertions.assertEquals(2, groupRepository.count())
    }

    @Test
    @Order(3)
    fun testUpdateGroupInformation() {
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
                .content(content),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/100/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content),
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
              "detailIntro": "string"
            }
        """.trimIndent()
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newRegionContent),
        ).andExpect(
            status().isNoContent,
        )
    }

    @Test
    @Order(3)
    fun testUpdateRecruitInformation() {
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/1/is-recruit/"),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/100/is-recruit/"),
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
                .content(content),
        ).andExpect(
            status().isNoContent,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/group/100/recruit-detail/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(4)
    fun testGetGroup() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/1/"),
        ).andExpect(
            status().isOk,
        ).andExpect(
            jsonPath("$.name").value("NEW_NAME"),
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/group/100/"),
        ).andExpect(
            status().isNotFound,
        )
    }

    @Test
    @Order(5)
    fun testGetGroupRecruitInformation() {
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
}
