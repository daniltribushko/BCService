package ru.tdd.geo.application.models.dto.geo.country;

import ru.tdd.geo.database.entities.Country;

import java.time.ZoneId;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO страны
 */
public class CountryDTO {

    private UUID id;

    private String name;

    private ZoneId zoneId;

    public CountryDTO() {}

    public CountryDTO(UUID id, String name, ZoneId zoneId) {
        this.id = id;
        this.name = name;
        this.zoneId = zoneId;
    }

    public static CountryDTO mapFromEntity(Country country) {
        return new CountryDTO(
                country.getId(),
                country.getName(),
                country.getZoneId()
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

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }
}
