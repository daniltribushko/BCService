package ru.tdd.geo.application.models.dto.geo.region;

import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;
import ru.tdd.geo.database.entities.Region;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * DTO Региона
 */
public class RegionDTO {

    private UUID id;

    private String name;

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
