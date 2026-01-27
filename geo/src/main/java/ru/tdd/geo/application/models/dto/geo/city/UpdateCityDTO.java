package ru.tdd.geo.application.models.dto.geo.city;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO запроса на обновление города
 */
public class UpdateCityDTO {

    private String name;

    private UUID regionId;

    private UUID countryId;

    public UpdateCityDTO() {}

    public UpdateCityDTO(String name, UUID regionId, UUID countryId) {
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
