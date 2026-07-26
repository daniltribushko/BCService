package ru.tdd.geo.application.models.dto.geo.country;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.geo.application.models.constants.OpenApiConstants;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO страны
 */
public class CountryDTO {

    @Schema(
            name = "id",
            description = "Идентификатор страны",
            type = "string",
            format = "uuid",
            example = OpenApiConstants.UUID_EXAMPLE
    )
    private UUID id;

    @Schema(
            name = "name",
            description = "Название страны",
            type = "string",
            example = "Russia"
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
