package com.umjari.server.global.config.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.umjari.server.domain.auth.JwtTokenProvider
import com.umjari.server.domain.auth.dto.AuthDto
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.io.BufferedReader

class CustomUsernamePasswordAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
) : UsernamePasswordAuthenticationFilter(authenticationManager) {
    init {
        setRequiresAuthenticationRequestMatcher(AntPathRequestMatcher("/api/v1/auth/login/", "POST"))
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication,
    ) {
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write(createAccessToken(authResult))
        response.status = HttpServletResponse.SC_OK
    }

    private fun createAccessToken(authResult: Authentication): String {
        val accessToken = mapOf("accessToken" to jwtTokenProvider.generateToken(authResult))
        return ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(accessToken)
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException,
    ) {
        super.unsuccessfulAuthentication(request, response, failed)
        response.status = HttpServletResponse.SC_UNAUTHORIZED
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val parseRequest = parseRequest(request)
        val authRequest = UsernamePasswordAuthenticationToken(parseRequest.userId, parseRequest.password)
        return authenticationManager.authenticate(authRequest)
    }

    private fun parseRequest(request: HttpServletRequest): AuthDto.LogInRequest {
        val reader: BufferedReader = request.reader
        val objectMapper = ObjectMapper()
        return objectMapper.readValue(reader, AuthDto.LogInRequest::class.java)
    }
}
