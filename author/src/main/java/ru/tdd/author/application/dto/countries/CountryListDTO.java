package ru.tdd.author.application.dto.countries;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
public class CountryListDTO {

    private List<CountryDTO> data;

    public CountryListDTO() {}

    public CountryListDTO(List<CountryDTO> data) {
        this.data = data;
    }

    public List<CountryDTO> getData() {
        return data;
    }

    public void setData(List<CountryDTO> data) {
        this.data = data;
    }
}
