package com.umjari.server.global.config.auth

import com.umjari.server.domain.auth.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer

class AuthenticationFilterDsl(
        private val jwtTokenProvider: JwtTokenProvider,
): AbstractHttpConfigurer<AuthenticationFilterDsl, HttpSecurity>() {
    override fun configure(builder: HttpSecurity) {
        val authenticationManager = builder.getSharedObject(AuthenticationManager::class.java)
        builder.addFilter(CustomUsernamePasswordAuthenticationFilter(authenticationManager, jwtTokenProvider))
                .addFilter(JwtAuthenticationFilter(authenticationManager, jwtTokenProvider))
    }
}