package ru.tdd.geo.application.models.dto.geo.country;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO запроса на обновление страны
 */
public class UpdateCountryDTO {

    @Schema(
            name = "name",
            description = "Название страны",
            example = "Russia",
            type = "string",
            nullable = true
    )
    private String name;

    public UpdateCountryDTO() {}

    public UpdateCountryDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
