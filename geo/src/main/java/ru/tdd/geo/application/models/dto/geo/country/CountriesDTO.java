package ru.tdd.geo.application.models.dto.geo.country;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO со списком стран
 */
public class CountriesDTO {

    @Schema(
            name = "data",
            description = "Список элементов"
    )
    private List<CountryDTO> data;

    public CountriesDTO() {}

    public CountriesDTO(List<CountryDTO> data) {
        this.data = data;
    }

    public List<CountryDTO> getData() {
        return data;
    }

    public void setData(List<CountryDTO> data) {
        this.data = data;
    }
}
