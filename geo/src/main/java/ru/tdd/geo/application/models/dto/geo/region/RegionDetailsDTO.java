package ru.tdd.geo.application.models.dto.geo.region;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.geo.application.models.constants.OpenApiConstants;
import ru.tdd.geo.application.models.dto.geo.city.CityDTO;
import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;

import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Детальная информация об регионе
 */
public class RegionDetailsDTO {

    @Schema(
            name = "id",
            description = "Идентификатор региона",
            type = "string",
            format = "uuid",
            example = OpenApiConstants.UUID_EXAMPLE
    )
    private UUID id;

    @Schema(
            name = "name",
            description = "Название региона",
            type = "string",
            example = "Moscow Oblast"
    )
    private String name;

    @Schema(
            name = "country",
            description = "Страна региона"
    )
    private CountryDTO countryDTO;

    @Schema(
            name = "cities",
            description = "Список городов региона"
    )
    private List<CityDTO> cities;

    public RegionDetailsDTO() {}

    public RegionDetailsDTO(UUID id, String name, CountryDTO countryDTO, List<CityDTO> cities) {
        this.id = id;
        this.name = name;
        this.countryDTO = countryDTO;
        this.cities = cities;
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

    public CountryDTO getCountryDTO() {
        return countryDTO;
    }

    public void setCountryDTO(CountryDTO countryDTO) {
        this.countryDTO = countryDTO;
    }

    public List<CityDTO> getCities() {
        return cities;
    }

    public void setCities(List<CityDTO> cities) {
        this.cities = cities;
    }
}
