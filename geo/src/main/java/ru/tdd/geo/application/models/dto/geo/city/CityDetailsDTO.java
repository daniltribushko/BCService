package ru.tdd.geo.application.models.dto.geo.city;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.geo.application.models.constants.OpenApiConstants;
import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.region.RegionDTO;
import ru.tdd.geo.database.entities.City;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO города с подробной информацикй
 */
public class CityDetailsDTO {

    @Schema(
            name = "id",
            description = "Идентификатор города",
            type = "string",
            format = "uuid",
            example = OpenApiConstants.UUID_EXAMPLE
    )
    private UUID id;

    @Schema(
            name = "name",
            description = "Название города",
            type = "string",
            example = "Moscow"
    )
    private String name;

    @Schema(
            name = "region",
            description = "Регион города"
    )
    private RegionDTO region;

    @Schema(
            name = "country",
            description = "Страна города"
    )
    private CountryDTO country;

    @Schema(
            name = "locations",
            description = "Список локаций города"
    )
    private List<LocationDTO> locations;

    public CityDetailsDTO() {}

    public CityDetailsDTO(UUID id, String name, RegionDTO region, CountryDTO country, List<LocationDTO> locations) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.country = country;
        this.locations = locations;
    }

    public static CityDetailsDTO mapFromEntity(City city) {
        return new CityDetailsDTO(
                city.getId(),
                city.getName(),
                Optional.ofNullable(city.getRegion()).map(RegionDTO::mapFromEntity).orElse(null),
                CountryDTO.mapFromEntity(city.getCountry()),
                city.getLocations()
                        .stream()
                        .map(LocationDTO::mapFromEntity)
                        .sorted(Comparator.comparing(LocationDTO::getName))
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

    public RegionDTO getRegion() {
        return region;
    }

    public void setRegion(RegionDTO region) {
        this.region = region;
    }

    public CountryDTO getCountry() {
        return country;
    }

    public void setCountry(CountryDTO country) {
        this.country = country;
    }

    public List<LocationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationDTO> locations) {
        this.locations = locations;
    }
}
