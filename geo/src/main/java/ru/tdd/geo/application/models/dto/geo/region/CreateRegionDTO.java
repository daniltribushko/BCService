package ru.tdd.geo.application.models.dto.geo.region;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.tdd.geo.application.models.constants.OpenApiConstants;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 */
public class CreateRegionDTO {

    @Schema(
            name = "name",
            description = "Название региона",
            type = "string",
            example = "Moscow Oblast"
    )
    @NotBlank(message = "Название региона не может быть пустым")
    private String name;

    @Schema(
            name = "country_id",
            description = "Идентификатор страны региона",
            type = "string",
            format = "uuid",
            example = OpenApiConstants.UUID_EXAMPLE
    )
    @NotNull(message = "Идентификатор страны не может быть пустым")
    private UUID countryId;

    public CreateRegionDTO() {}

    public CreateRegionDTO(String name, UUID countryId) {
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
