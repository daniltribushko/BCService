package ru.tdd.geo.application.models.dto.geo.country;

import java.time.ZoneId;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO запроса на обновление страны
 */
public class UpdateCountryDTO {

    private String name;

    private ZoneId zoneId;

    public UpdateCountryDTO() {}

    public UpdateCountryDTO(String name, ZoneId zoneId) {
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
