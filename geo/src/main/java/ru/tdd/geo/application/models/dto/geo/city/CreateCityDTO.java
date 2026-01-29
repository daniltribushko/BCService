package ru.tdd.geo.application.models.dto.geo.city;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import ru.tdd.geo.application.models.constants.OpenApiConstants;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO запроса на создание города
 */
public class CreateCityDTO {

    @Schema(
            name = "name",
            description = "Название города"
    )
    @NotBlank(message = "Название города обязательно для заполнения")
    private String name;

    @Schema(
            name = "region_id",
            description = "Идентификатор региона",
            type = "string",
            format = "uuid",
            example = OpenApiConstants.UUID_EXAMPLE,
            nullable = true
    )
    private UUID regionId;

    @Schema(
            name = "country_id",
            description = "Идентификатор страны",
            type = "string",
            format = "uuid",
            example = OpenApiConstants.UUID_EXAMPLE
    )
    @NotBlank(message = "Идентификатор страны обязателен для заполнения")
    private UUID countryId;

    public CreateCityDTO() {}

    public CreateCityDTO(String name, UUID regionId, UUID countryId) {
        this.name = name;
        this.regionId = regionId;
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getRegionId() {
        return regionId;
    }

    public void setRegionId(UUID regionId) {
        this.regionId = regionId;
    }

    public UUID getCountryId() {
        return countryId;
    }

    public void setCountryId(UUID countryId) {
        this.countryId = countryId;
    }
}
