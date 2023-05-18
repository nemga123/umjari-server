package com.umjari.server.utils

import com.jayway.jsonpath.JsonPath
import com.umjari.server.domain.group.model.Group
import com.umjari.server.domain.group.repository.GroupRepository
import com.umjari.server.domain.mailverification.model.VerifyToken
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.region.model.Region
import com.umjari.server.domain.region.repository.RegionRepository
import com.umjari.server.domain.user.model.User
import com.umjari.server.domain.user.repository.UserRepository
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class TestUtils {
    companion object {
        fun createDummyGroup(regionRepository: RegionRepository, groupRepository: GroupRepository): Group {
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
            return groupRepository.save(group)
        }

        fun createDummyUser(
            mockMvc: MockMvc,
            userRepository: UserRepository,
            verifyTokenRepository: VerifyTokenRepository,
            email: String = "user@umjari.co.kr",
            userId: String = "user",
            profileName: String = "홍길동",
            nickname: String = "user",
        ): Pair<User, String> {
            val verificationToken = VerifyToken(
                token = "TOKEN1",
                email = email,
                confirmed = true,
            )
            verifyTokenRepository.save(verificationToken)
            val signUpRequest = """
                {
                    "userId": "$userId",
                    "password": "password",
                    "profileName":"$profileName",
                    "email": "$email",
                    "nickname": "$nickname",
                    "intro": "intro"
                }
            """.trimIndent()

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(signUpRequest),
            ).andExpect(
                MockMvcResultMatchers.status().isNoContent,
            )

            val logInRequest = """
                {
                    "userId": "$userId",
                    "password": "password"
                }
            """.trimIndent()

            val result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/login/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(logInRequest),
            ).andExpect(
                MockMvcResultMatchers.status().isOk,
            ).andReturn()

            return Pair(
                userRepository.findByUserId("$userId")!!,
                JsonPath.read(result.response.contentAsString, "$.accessToken"),
            )
        }

        fun createDummyAdmin(
            mockMvc: MockMvc,
            userRepository: UserRepository,
            verifyTokenRepository: VerifyTokenRepository,
        ): Pair<User, String> {
            val verificationToken = VerifyToken(
                token = "TOKEN2",
                email = "admin@umjari.co.kr",
                confirmed = true,
            )
            verifyTokenRepository.save(verificationToken)
            val signUpRequest = """
                {
                    "userId": "user2",
                    "password": "password",
                    "profileName":"user2",
                    "email": "admin@umjari.co.kr",
                    "nickname": "user2",
                    "intro": "intro"
                }
            """.trimIndent()

            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(signUpRequest),
            ).andExpect(
                MockMvcResultMatchers.status().isNoContent,
            )

            val admin = userRepository.findByUserId("user2")!!
            admin.roles = "ROLE_USER,ROLE_ADMIN"
            userRepository.save(admin)

            val logInRequest = """
                {
                    "userId": "user2",
                    "password": "password"
                }
            """.trimIndent()

            val result = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/auth/login/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(logInRequest),
            ).andExpect(
                MockMvcResultMatchers.status().isOk,
            ).andReturn()

            return Pair(
                admin,
                JsonPath.read(result.response.contentAsString, "$.accessToken"),
            )
        }
    }
}
