package ru.tdd.book.controllers.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tdd.core.application.annotations.open_api.OpenApiSecurityScheme;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 02.05.2026
 * Конфигурация OpenApi
 */
@Configuration
@OpenApiSecurityScheme
public class OpenApiConfig {

    public static final String GENRE_CONTROLLER = "Genre Controller";

    public static final String BOOK_CONTROLLER = "Book Controller";

    public static final String AUTHOR_CONTROLLER = "Author Controller";

    public static final String COUNTRY_CONTROLLER = "Country Controller";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Book Service")
                                .description("Сервис для работы с книгами")
                )
                .servers(
                        List.of(
                                new Server()
                                        .url("/v1/api")
                        )
                )
                .tags(
                        List.of(
                                new Tag()
                                        .name(GENRE_CONTROLLER)
                                        .description("Контроллер для работы с жанрами"),
                                new Tag()
                                        .name(BOOK_CONTROLLER)
                                        .description("Контроллер для работы с книгами"),
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
