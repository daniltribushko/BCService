package ru.tdd.geo.application.models.dto.geo.region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 */
public class CreateRegionDTO {

    @NotBlank(message = "Название региона не может быть пустым")
    private String name;

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
