package com.umjari.server.global.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@EnableWebMvc
class SwaggerConfig {
    @Bean
    fun springOpenApi(): OpenAPI {
        return OpenAPI()
                .components(Components())
                .info(
                    Info().title("Umjari API")
                            .description("Umjari API Docs")
                            .version("v1")
                )
    }
}