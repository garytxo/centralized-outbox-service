package com.murray.outbox.shared.infrastructure.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import jakarta.servlet.ServletContext
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {


    @Bean
    fun notificationApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("default")
            .pathsToMatch("/**")
            .build()
    }

    @Bean
    fun metaData(servletContext: ServletContext): OpenAPI {
        val server = Server().url(servletContext.contextPath)
        return OpenAPI()
            .servers(listOf(server))
            .info(
                Info().title("Centralized Outbox REST API")
                    .description("Centralized Outbox REST API")
                    .version("v0.0.1")
            ).externalDocs(
                ExternalDocumentation()
                    .description("Centralized Outbox service Documentation")
                    .url("https://github.com/garytxo/centralized-outbox-service")
            )
    }


}