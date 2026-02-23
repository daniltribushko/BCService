package ru.tdd.author.application.dto.countries;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 19.02.2026
 */
public class CountryDTO {

    @Schema(
            name = "id",
            description = "Идентификатор страны",
            type = "string",
            format = "uuid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID id;

    @Schema(
            name = "name",
            description = "Название страны",
            type = "string",
            example = "Россия",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    public CountryDTO() {}

    public CountryDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
