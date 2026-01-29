package ru.tdd.geo.application.models.dto.geo.region;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.geo.application.models.constants.OpenApiConstants;
import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;
import ru.tdd.geo.database.entities.Region;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * DTO Региона
 */
public class RegionDTO {

    @Schema(
            name = "id",
            description = "Идентификатор региона",
            type = "string",
            format = "uuid",
            example = OpenApiConstants.UUID_EXAMPLE
    )
    private UUID id;

    @Schema(
            name = "name",
            description = "Название региона",
            type = "string",
            example = "Moscow Oblast"
    )
    private String name;

    @Schema(
            name = "country",
            description = "Страна региона"
    )
    private CountryDTO country;

    public RegionDTO() {}

    public RegionDTO(UUID id, String name, CountryDTO country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public static RegionDTO mapFromEntity(Region region) {
        return new RegionDTO(
                region.getId(),
                region.getName(),
                CountryDTO.mapFromEntity(region.getCountry())
        );
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

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }
}
