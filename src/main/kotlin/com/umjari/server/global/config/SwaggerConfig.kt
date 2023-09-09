package com.umjari.server.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@EnableWebMvc
class SwaggerConfig {
    @Bean
    fun springOpenApi(): OpenAPI {
        val info = Info().title("Umjari API")
            .description("Umjari API Docs")
            .version("v1")

        val securityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER).name("Authorization")

        val securityRequirement = SecurityRequirement().addList("bearerAuth")

        val server = Server().also { server -> server.url = "/" }

        return OpenAPI()
            .servers(listOf(server))
            .components(Components().addSecuritySchemes("bearerAuth", securityScheme))
            .security(listOf(securityRequirement))
            .info(info)
    }
}
