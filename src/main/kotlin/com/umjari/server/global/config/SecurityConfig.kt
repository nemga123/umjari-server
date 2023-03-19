package com.umjari.server.global.config

import com.umjari.server.domain.auth.JwtTokenProvider
import com.umjari.server.global.config.auth.AuthenticationFilterDsl
import com.umjari.server.global.config.auth.JwtAuthenticationEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

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
            .requestMatchers(AntPathRequestMatcher("/api/v1/auth/login/", "POST")).permitAll()
            .requestMatchers(AntPathRequestMatcher("/api/v1/auth/signup/", "POST")).permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers("/api/v1/ping/").permitAll()
            .requestMatchers("/api/v1/user/me/").authenticated()
            .anyRequest().permitAll()
        return httpSecurity.build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers(
                    "/css/**",
                    "/images/**",
                    "/js/**",
                    // -- Swagger UI v3 (Open API)
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                )
        }
    }
}
