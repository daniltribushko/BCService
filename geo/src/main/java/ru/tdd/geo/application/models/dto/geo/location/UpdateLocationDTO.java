package ru.tdd.geo.application.models.dto.geo.location;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.geo.application.models.constants.OpenApiConstants;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO запроса на обновление локации
 */
public class UpdateLocationDTO {

    @Schema(
            name = "name",
            description = "Название локации",
            type = "string",
            example = "Test Location",
            nullable = true
    )
    private String name;

    @Schema(
            name = "city_id",
            description = "Идентификатор города локации",
            type = "string",
            format = "uuid",
            example = OpenApiConstants.UUID_EXAMPLE,
            nullable = true
    )
    private UUID cityId;

    public UpdateLocationDTO() {}

    public UpdateLocationDTO(String name, UUID cityId) {
        this.name = name;
        this.cityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getCityId() {
        return cityId;
    }

    public void setCityId(UUID cityId) {
        this.cityId = cityId;
    }
}
