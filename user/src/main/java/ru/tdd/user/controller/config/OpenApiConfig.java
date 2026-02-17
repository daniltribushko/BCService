package ru.tdd.user.controller.config;

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
 * @since 10.02.2026
 */
@Configuration
@SecurityScheme(
        name = "jwtAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    public static final String AUTH_CONTROLLER = "Auth Controller";

    public static final String USER_CONTROLLER = "User Controller";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .openapi("3.0.0")
                .info(
                        new Info()
                                .title("User Service")
                                .version("1.0.0")
                                .description("Сервис для работы с пользователями")
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
                                        .name(AUTH_CONTROLLER)
                                        .description("Контроллер для авторизации и регистрации пользователей"),
                                new Tag()
                                        .name(USER_CONTROLLER)
                                        .description("Контроллер для работы с пользователями")
                        )
                );
    }
}
