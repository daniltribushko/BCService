package ru.tdd.geo.controller.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "jwtAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    public static final String COUNTRY_CONTROLLER = "Country Controller";

    public static final String REGION_CONTROLLER = "Region Controller";

    public static final String CITY_CONTROLLER = "City Controller";

    public static final String LOCATION_CONTROLLER = "Location Controller";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().
                info(
                        new Info()
                                .title("BC Geo Service")
                                .description("Сервис для работы со Странами, Регионами, Городами, Локациями")
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
                                        .name(COUNTRY_CONTROLLER)
                                        .description("Эндпоинты для работы со странами"),
                                new Tag()
                                        .name(REGION_CONTROLLER)
                                        .description("Эндпоинты для работы с регионами"),
                                new Tag()
                                        .name(CITY_CONTROLLER)
                                        .description("Эндпоинты для работы с городами"),
                                new Tag()
                                        .name(LOCATION_CONTROLLER)
                                        .description("Эндпоинты для работы с локациями")
                        )
                );
    }
}
