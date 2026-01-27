package ru.tdd.geo.application.models.dto.geo.country;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO запроса на создание страны
 */
public class CreateCountryDTO {

    @NotBlank(message = "Название страны не может быть пустым")
    private String name;

    @NotNull(message = "Часовой пояс не может быть пустым")
    private ZoneId zoneId;

    public CreateCountryDTO() {}

    public CreateCountryDTO(String name, ZoneId zoneId) {
        this.name = name;
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }
}
