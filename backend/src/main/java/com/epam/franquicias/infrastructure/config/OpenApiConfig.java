package com.epam.franquicias.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI franchisesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Franquicias (EPAM Challenge)")
                        .description("API REST implementada bajo principios SOLID y Clean Architecture (Hexagonal Architecture) con Spring Boot y MongoDB.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("dev@franquicias.epam.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .servers(List.of(
                        new Server().url("/").description("Servidor Actual / Relativo (Recomendado)"),
                        new Server().url("http://138.199.212.52:8781").description("Servidor Producción (138.199.212.52:8781)"),
                        new Server().url("http://localhost:8781").description("Servidor Local (localhost:8781)")
                ));
    }
}
