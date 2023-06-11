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
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

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
            .cors()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .apply(AuthenticationFilterDsl(jwtTokenProvider))
            .and()
            .authorizeHttpRequests()
            .requestMatchers(
                AntPathRequestMatcher("/api/v1/auth/login/", "POST"),
                AntPathRequestMatcher("/api/v1/auth/signup/", "POST"),
                AntPathRequestMatcher("/api/v1/user/nickname/", "POST"),
                AntPathRequestMatcher("/api/v1/mail-verification/", "POST"),
                AntPathRequestMatcher("/api/v1/mail-verification/validate/", "POST"),
            ).permitAll()
            .requestMatchers(
                AntPathRequestMatcher("/api/v1/group/{\\d+}/", "GET"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/recruit/", "GET"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/concerts/", "GET"),
                AntPathRequestMatcher("/api/v1/concert/dashboard/", "GET"),
                AntPathRequestMatcher("/api/v1/concert/{\\d+}/", "GET"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/qna/", "GET"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/qna/{\\d+}/", "GET"),
                AntPathRequestMatcher("/api/v1/user/profile-name/**/", "GET"),
                AntPathRequestMatcher("/api/v1/music/", "GET"),
                AntPathRequestMatcher("/api/v1/concert/{\\d+}/concert-music/{\\d+}/participant/", "GET"),
                AntPathRequestMatcher("/api/v1/concert/{\\d+}/participant/", "GET"),
                AntPathRequestMatcher("/api/v1/user/profile-name/**/joined-concert/", "GET"),
                AntPathRequestMatcher("/api/v1/user/profile-name/**/joined-concert/poster/", "GET"),
                AntPathRequestMatcher("/api/v1/board/**/post/", "GET"),
                AntPathRequestMatcher("/api/v1/board/**/post/{\\d+}/", "GET"),
                AntPathRequestMatcher("/api/v1/album/profile-name/**/", "GET"),
                AntPathRequestMatcher("/api/v1/album/{\\d+}/photo/", "GET"),
            ).permitAll()
            .requestMatchers(
                AntPathRequestMatcher("/api/v1/group/{\\d+}/", "PUT"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/is-recruit/", "PUT"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/recruit-detail/", "PUT"),
                AntPathRequestMatcher("/api/v1/concert/group/{\\d+}/", "POST"),
                AntPathRequestMatcher("/api/v1/concert/{\\d+}/details/", "PUT"),
                AntPathRequestMatcher("/api/v1/concert/{\\d+}/info/", "PUT"),
                AntPathRequestMatcher("/api/v1/concert/{\\d+}/set-list/", "PUT"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/qna/", "POST"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/qna/{\\d+}/", "PUT"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/qna/{\\d+}/reply/", "POST"),
                AntPathRequestMatcher("/api/v1/image/", "POST"),
                AntPathRequestMatcher("/api/v1/image/", "DELETE"),
                AntPathRequestMatcher("/api/v1/user/my-group/", "GET"),
                AntPathRequestMatcher("/api/v1/user/info/", "PUT"),
                AntPathRequestMatcher("/api/v1/music/", "POST"),
                AntPathRequestMatcher("/api/v1/concert/{\\d+}/concert-music/{\\d+}/participant/", "PUT"),
                AntPathRequestMatcher("/api/v1/concert/{\\d+}/concert-music/{\\d+}/participant/", "DELETE"),
                AntPathRequestMatcher("/api/v1/board/**/post/", "POST"),
                AntPathRequestMatcher("/api/v1/board/**/post/{\\d+}/", "PUT"),
                AntPathRequestMatcher("/api/v1/board/**/post/{\\d+}/", "DELETE"),
                AntPathRequestMatcher("/api/v1/board/**/post/{\\d+}/reply/", "POST"),
                AntPathRequestMatcher("/api/v1/board/**/post/{\\d+}/reply/{\\d+}/", "PUT"),
                AntPathRequestMatcher("/api/v1/board/**/post/{\\d+}/reply/{\\d+}/", "DELETE"),
                AntPathRequestMatcher("/api/v1/album/", "POST"),
                AntPathRequestMatcher("/api/v1/album/{\\d+}/", "PUT"),
                AntPathRequestMatcher("/api/v1/album/{\\d+}/", "DELETE"),
                AntPathRequestMatcher("/api/v1/album/{\\d+}/photo/", "POST"),
                AntPathRequestMatcher("/api/v1/album/{\\d+}/photo/", "DELETE"),
                AntPathRequestMatcher("/api/v1/post/{\\d+}/likes/", "PUT"),
            ).hasRole("USER")
            .requestMatchers(
                AntPathRequestMatcher("/api/v1/group/", "POST"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/register/", "POST"),
                AntPathRequestMatcher("/api/v1/group/{\\d+}/register/admin/", "POST"),
            ).hasRole("ADMIN")
            .requestMatchers("/error").permitAll()
            .requestMatchers("/api/v1/ping/").permitAll()
            .requestMatchers("/api/v1/user/me/").authenticated()
            .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
            .anyRequest().authenticated()
        return httpSecurity.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = mutableListOf("http://localhost", "https://umjari.co.kr")
        configuration.allowedMethods = mutableListOf("*")
        configuration.allowedHeaders = mutableListOf("Content-Type", "Authorization")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
