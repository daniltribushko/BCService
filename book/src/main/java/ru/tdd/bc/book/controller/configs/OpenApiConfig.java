package ru.tdd.bc.book.controller.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tdd.bc.openapi.annotations.OpenApiSecurityScheme;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 */
@Configuration
@OpenApiSecurityScheme
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

