package ru.tdd.geo.application.models.dto.geo.region;

import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;
import ru.tdd.geo.database.entities.Region;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Детальная информация об регионе
 */
public class RegionDetailsDTO {

    private UUID id;

    private String name;

    private CountryDTO countryDTO;

    public RegionDetailsDTO() {}

    public RegionDetailsDTO(UUID id, String name, CountryDTO countryDTO) {
        this.id = id;
        this.name = name;
        this.countryDTO = countryDTO;
    }

    public static RegionDetailsDTO mapFromEntity(Region region) {
        return new RegionDetailsDTO(
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

    public CountryDTO getCountryDTO() {
        return countryDTO;
    }

    public void setCountryDTO(CountryDTO countryDTO) {
        this.countryDTO = countryDTO;
    }
}
