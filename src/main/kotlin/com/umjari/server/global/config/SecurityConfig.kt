package com.umjari.server.global.config

import com.umjari.server.domain.auth.JwtTokenProvider
import com.umjari.server.global.config.auth.AuthenticationFilterDsl
import com.umjari.server.global.config.auth.JwtAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity(debug = true)
class SecurityConfig(
        private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
        private val jwtTokenProvider: JwtTokenProvider,
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .apply(AuthenticationFilterDsl(jwtTokenProvider))
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/login/**").permitAll()
                .requestMatchers("/api/v1/auth/signup/**").permitAll()
                .requestMatchers("/api/v1/ping/**").authenticated()
                .anyRequest().permitAll()
        return httpSecurity.build()
    }
}
