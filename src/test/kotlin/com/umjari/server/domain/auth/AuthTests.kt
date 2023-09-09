package com.umjari.server.domain.auth

import com.umjari.server.domain.mailverification.model.VerifyToken
import com.umjari.server.domain.mailverification.repository.VerifyTokenRepository
import com.umjari.server.domain.user.repository.UserRepository
import com.umjari.server.global.exception.ErrorType
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class AuthTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var verifyTokenRepository: VerifyTokenRepository

    @Test
    @Order(1)
    fun testSignUp() {
        val verificationToken = VerifyToken(
            token = "TOKEN1",
            email = "user1@umjari.co.kr",
            confirmed = true,
        )
        verifyTokenRepository.save(verificationToken)
        val signUpRequest = """
                {
                    "userId": "user1",
                    "password": "password",
                    "profileName":"user1",
                    "email": "user1@umjari.co.kr",
                    "nickname": "user1",
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

        val signUpWithNotVerifiedTokenRequest = """
                {
                    "userId": "user2",
                    "password": "password",
                    "profileName":"홍길동",
                    "email": "user2@umjari.co.kr",
                    "nickname": "user2",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpWithNotVerifiedTokenRequest),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        )
    }

    @Test
    @Order(1)
    fun testSignUpWithUnverifiedEmail() {
        val verificationToken = VerifyToken(
            token = "TOKEN2",
            email = "user2@umjari.co.kr",
            confirmed = false,
        )
        verifyTokenRepository.save(verificationToken)
        val user2SignUpRequest = """
                {
                    "userId": "user2",
                    "password": "password",
                    "profileName":"user2",
                    "email": "user2@umjari.co.kr",
                    "nickname": "user2",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user2SignUpRequest),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        )

        val user3SignUpRequest = """
                {
                    "userId": "user3",
                    "password": "password",
                    "profileName":"user3",
                    "email": "user3@umjari.co.kr",
                    "nickname": "user3",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user3SignUpRequest),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        )
    }

    @Test
    @Order(2)
    fun testSignUpWithDuplicatedData() {
        val verificationToken = VerifyToken(
            token = "TOKEN4",
            email = "user4@umjari.co.kr",
            confirmed = true,
        )
        verifyTokenRepository.save(verificationToken)

        val duplicatedUserIdRequest = """
                {
                    "userId": "user1",
                    "password": "password",
                    "profileName":"user2",
                    "email": "user4@umjari.co.kr",
                    "nickname": "user2",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicatedUserIdRequest),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        ).andExpect(
            jsonPath("$.errorCode").value(ErrorType.DUPLICATED_USER_ID.code),
        )

        val duplicatedUserNicknameRequest = """
                {
                    "userId": "user2",
                    "password": "password",
                    "profileName":"user2",
                    "email": "user4@umjari.co.kr",
                    "nickname": "user1",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicatedUserNicknameRequest),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        ).andExpect(
            jsonPath("$.errorCode").value(ErrorType.DUPLICATED_USER_NICKNAME.code),
        )

        val duplicatedUserProfileNameRequest = """
                {
                    "userId": "user2",
                    "password": "password",
                    "profileName":"user1",
                    "email": "user4@umjari.co.kr",
                    "nickname": "user2",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicatedUserProfileNameRequest),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        ).andExpect(
            jsonPath("$.errorCode").value(ErrorType.DUPLICATED_USER_PROFILE_NAME.code),
        )

        val duplicatedEmailVerificationToken = VerifyToken(
            token = "TOKEN4",
            email = "user1@umjari.co.kr",
            confirmed = true,
        )
        verifyTokenRepository.save(duplicatedEmailVerificationToken)
        val duplicatedEmailRequest = """
                {
                    "userId": "user2",
                    "password": "password",
                    "profileName":"user2",
                    "email": "user1@umjari.co.kr",
                    "nickname": "user2",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicatedEmailRequest),
        ).andExpect(
            MockMvcResultMatchers.status().isBadRequest,
        ).andExpect(
            jsonPath("$.errorCode").value(ErrorType.DUPLICATED_USER_EMAIL.code),
        )
    }

    @Test
    @Order(4)
    fun testSignUpWithRegion(
        @Autowired userRepository: UserRepository,
    ) {
        val verificationToken = VerifyToken(
            token = "TOKEN5",
            email = "user5@umjari.co.kr",
            confirmed = true,
        )
        verifyTokenRepository.save(verificationToken)
        val signUpRequest = """
                {
                    "userId": "user5",
                    "password": "password",
                    "profileName":"user5",
                    "email": "user5@umjari.co.kr",
                    "nickname": "user5",
                    "intro": "intro",
                    "regionParent": "서울시",
                    "regionChild": "관악구"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )

        val verificationToken2 = VerifyToken(
            token = "TOKEN6",
            email = "user6@umjari.co.kr",
            confirmed = true,
        )
        verifyTokenRepository.save(verificationToken2)
        val signUpRequest2 = """
                {
                    "userId": "user6",
                    "password": "password",
                    "profileName":"user6",
                    "email": "user6@umjari.co.kr",
                    "nickname": "user6",
                    "intro": "intro",
                    "regionParent": "서울시"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest2),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )

        val verificationToken3 = VerifyToken(
            token = "TOKEN7",
            email = "user7@umjari.co.kr",
            confirmed = true,
        )
        verifyTokenRepository.save(verificationToken3)
        val signUpRequest3 = """
                {
                    "userId": "user7",
                    "password": "password",
                    "profileName":"user7",
                    "email": "user7@umjari.co.kr",
                    "nickname": "user7",
                    "intro": "intro",
                    "regionChild": "관악구"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest3),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )

        val verificationToken4 = VerifyToken(
            token = "TOKEN8",
            email = "user8@umjari.co.kr",
            confirmed = true,
        )
        verifyTokenRepository.save(verificationToken4)
        val signUpRequest4 = """
                {
                    "userId": "user8",
                    "password": "password",
                    "profileName":"user8",
                    "email": "user8@umjari.co.kr",
                    "nickname": "user8",
                    "intro": "intro"
                }
        """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/auth/signup/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest4),
        ).andExpect(
            MockMvcResultMatchers.status().isNoContent,
        )

        assert(userRepository.findByEmail("user8@umjari.co.kr")!!.region == " ")
    }
}
