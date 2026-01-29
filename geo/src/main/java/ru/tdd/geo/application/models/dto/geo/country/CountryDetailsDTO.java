package ru.tdd.geo.application.models.dto.geo.country;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.geo.application.models.constants.OpenApiConstants;
import ru.tdd.geo.application.models.dto.geo.region.RegionDTO;
import ru.tdd.geo.database.entities.Country;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO страны с подробной информацией
 */
public class CountryDetailsDTO {

    @Schema(
            name = "id",
            description = "Идентификатор страны",
            type = "string",
            example = OpenApiConstants.UUID_EXAMPLE,
            format = "uuid"
    )
    private UUID id;

    @Schema(
            name = "name",
            description = "Название страны",
            type = "string",
            example = "Russia"
    )
    private String name;

    private List<RegionDTO> regions;

    public CountryDetailsDTO() {}

    public CountryDetailsDTO(UUID id, String name, List<RegionDTO> regions) {
        this.id = id;
        this.name = name;
        this.regions = regions;
    }

    public static CountryDetailsDTO mapFromEntity(Country country) {
        return new CountryDetailsDTO(
                country.getId(),
                country.getName(),
                country.getRegions().stream().map(RegionDTO::mapFromEntity)
                        .sorted(Comparator.comparing(RegionDTO::getName))
                        .toList()

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

    public List<RegionDTO> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionDTO> regions) {
        this.regions = regions;
    }
}
