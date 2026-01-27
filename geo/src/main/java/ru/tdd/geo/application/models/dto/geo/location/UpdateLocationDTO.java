package ru.tdd.geo.application.models.dto.geo.location;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO запроса на обновление локации
 */
public class UpdateLocationDTO {

    private String name;

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
