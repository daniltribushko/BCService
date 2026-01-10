package ru.tdd.geo.application.models.dto.geo.region;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * DTO запроса на обновление региона
 */
public class UpdateRegionDTO {

    private String name;

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
