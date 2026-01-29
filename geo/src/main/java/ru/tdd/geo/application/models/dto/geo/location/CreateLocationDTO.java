package ru.tdd.geo.application.models.dto.geo.location;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.tdd.geo.application.models.constants.OpenApiConstants;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO запроса на создание локации
 */
public class CreateLocationDTO {

    @Schema(
            name = "name",
            description = "Название локации",
            type = "string",
            example = "Test Location"
    )
    @NotBlank(message = "Название локации обязательно для заполнения")
    private String name;

    @Schema(
            name = "city_id",
            description = "Идентификатор города",
            type = "string",
            format = "uuid",
            example = OpenApiConstants.UUID_EXAMPLE,
            nullable = true
    )
    @NotNull(message = "Идентификатор города обязателен для заполнения")
    private UUID city;

    public CreateLocationDTO() {}

    public CreateLocationDTO(String name, UUID city) {
        this.name = name;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getCity() {
        return city;
    }

    public void setCity(UUID city) {
        this.city = city;
    }
}
