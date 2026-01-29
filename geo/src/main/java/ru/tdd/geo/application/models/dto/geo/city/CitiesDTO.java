package ru.tdd.geo.application.models.dto.geo.city;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 11.01.2026
 * DTO списка городов
 */
public class CitiesDTO {

    @Schema(
            name = "data",
            description = "Список городов"
    )
    private List<CityDTO> data;

    public CitiesDTO() {}

    public CitiesDTO(List<CityDTO> data) {
        this.data = data;
    }

    public List<CityDTO> getData() {
        return data;
    }

    public void setData(List<CityDTO> data) {
        this.data = data;
    }
}
