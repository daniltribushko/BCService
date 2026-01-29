package ru.tdd.geo.application.models.dto.geo.location;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.geo.database.entities.Location;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO списка локаций
 */
public class LocationsDTO {

    @Schema(
            name = "data",
            description = "Список локаций"
    )
    private List<LocationDTO> data;

    public LocationsDTO() {}

    public LocationsDTO(List<LocationDTO> data) {
        this.data = data;
    }

    public List<LocationDTO> getData() {
        return data;
    }

    public void setData(List<LocationDTO> data) {
        this.data = data;
    }
}
