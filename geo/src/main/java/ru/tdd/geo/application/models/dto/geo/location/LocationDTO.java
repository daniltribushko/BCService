package ru.tdd.geo.application.models.dto.geo.location;

import ru.tdd.geo.application.models.dto.geo.city.CityDTO;
import ru.tdd.geo.database.entities.Location;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO локации
 */
public class LocationDTO {

    private UUID id;

    private String name;

    private CityDTO city;

    public LocationDTO(){}

    public LocationDTO(UUID id, String name, CityDTO city) {
        this.id = id;
        this.name = name;
        this.city = city;
    }

    public static LocationDTO mapFromEntity(Location location) {
        return new LocationDTO(
            location.getId(),
                location.getName(),
                CityDTO.mapFromEntity(location.getCity())
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

    public CityDTO getCity() {
        return city;
    }

    public void setCity(CityDTO city) {
        this.city = city;
    }
}
