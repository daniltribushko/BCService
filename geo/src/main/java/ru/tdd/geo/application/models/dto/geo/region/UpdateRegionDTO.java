package ru.tdd.geo.application.models.dto.geo.region;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.geo.application.models.constants.OpenApiConstants;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * DTO запроса на обновление региона
 */
public class UpdateRegionDTO {

    @Schema(
            name = "name",
            description = "Название региона",
            type = "string",
            example = "Moscow Oblast",
            nullable = true
    )
    private String name;

    @Schema(
            name = "country_id",
            description = "Идентификатор страны региона",
            type = "String",
            format = "uuid",
            example = OpenApiConstants.UUID_EXAMPLE,
            nullable = true
    )
    private UUID countryId;

    public UpdateRegionDTO() {}

    public UpdateRegionDTO(String name, UUID countryId) {
        this.name = name;
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getCountryId() {
        return countryId;
    }

    public void setCountryId(UUID countryId) {
        this.countryId = countryId;
    }
}
