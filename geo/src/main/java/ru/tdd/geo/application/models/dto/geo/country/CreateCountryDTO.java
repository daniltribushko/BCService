package ru.tdd.geo.application.models.dto.geo.country;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO запроса на создание страны
 */
public class CreateCountryDTO {

    @Schema(
            name = "name",
            description = "Название страны",
            example = "Russia",
            type = "string"
    )
    @NotBlank(message = "Название страны не может быть пустым")
    private String name;

    public CreateCountryDTO() {}

    public CreateCountryDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
