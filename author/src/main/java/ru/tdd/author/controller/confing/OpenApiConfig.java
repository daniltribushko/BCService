package ru.tdd.author.controller.confing;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 */
@Configuration
@SecurityScheme(
        name = "jwtAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"

)
public class OpenApiConfig {

    public static final String AUTHOR_CONTROLLER = "Author Controller";

    public static final String COUNTRY_CONTROLLER = "Country Controller";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Author Service")
                                .description("Микросервис дял работы с авторами книг")
                )
                .servers(
                        List.of(
                                new Server()
                                        .url("/api/v1")
                        )
                )
                .tags(
                        List.of(
                                new Tag()
                                        .name(AUTHOR_CONTROLLER)
                                        .description("Контроллер для работы с авторами"),
                                new Tag()
                                        .name(COUNTRY_CONTROLLER)
                                        .description("Контроллер для работы со странами")
                        )
                );
    }
}
