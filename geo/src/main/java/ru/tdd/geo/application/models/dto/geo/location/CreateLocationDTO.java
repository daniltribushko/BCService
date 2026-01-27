package ru.tdd.geo.application.models.dto.geo.location;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO запроса на создание локации
 */
public class CreateLocationDTO {

    private String name;

    private UUID city;

    public CreateLocationDTO() {}

    public CreateLocationDTO(String name, UUID city) {
        this.name = name;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getCity() {
        return city;
    }

    public void setCity(UUID city) {
        this.city = city;
    }
}
