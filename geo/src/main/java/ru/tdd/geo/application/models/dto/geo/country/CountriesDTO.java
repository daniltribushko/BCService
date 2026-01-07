package ru.tdd.geo.application.models.dto.geo.country;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * DTO со списком стран
 */
public class CountriesDTO {

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
