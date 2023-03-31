package com.umjari.server.domain.group

import com.umjari.server.domain.group.repository.GroupRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.log
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GroupTests{
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Test
    fun testCreateGroup(){
        val content = """
            {
              "name": "GROUP_NAME",
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
                .content(content)
        ).andExpect(
            status().isCreated()
        )
        Assertions.assertEquals(groupRepository.count(), 1)
    }

    @Test
    fun testCreateGroupWithDefaultLogo(){
        val content = """
            {
              "name": "GROUP_NAME",
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
                .content(content)
        ).andExpect(
            status().isCreated()
        )
        Assertions.assertEquals(groupRepository.count(), 2)
    }
}
