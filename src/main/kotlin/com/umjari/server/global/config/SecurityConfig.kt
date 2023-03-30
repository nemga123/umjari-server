package com.umjari.server.global.config

import com.umjari.server.domain.auth.JwtTokenProvider
import com.umjari.server.global.auth.JwtAuthenticationEntryPoint
import com.umjari.server.global.auth.filter.AuthenticationFilterDsl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
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
            .requestMatchers(AntPathRequestMatcher("/api/v1/auth/login/", "POST")).permitAll()
            .requestMatchers(AntPathRequestMatcher("/api/v1/auth/signup/", "POST")).permitAll()
            .requestMatchers(
                AntPathRequestMatcher("/api/v1/group/", "POST"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/", "GET"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/recruit/", "GET"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/concerts/", "GET"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/", "PUT"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/is-recruit/", "PUT"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/recruit-detail/", "PUT"),
            ).permitAll()
            .requestMatchers(
                AntPathRequestMatcher("/api/v1/concert/group/{\\d+}/", "POST"),
                AntPathRequestMatcher("/api/v1/concert/{\\d+}/", "GET"),
            ).permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers("/api/v1/ping/").permitAll()
            .requestMatchers("/api/v1/user/me/").authenticated()
            .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
            .anyRequest().authenticated()
        return httpSecurity.build()
    }
}
