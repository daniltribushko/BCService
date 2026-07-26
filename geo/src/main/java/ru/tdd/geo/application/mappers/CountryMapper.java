package ru.tdd.geo.application.mappers;

import org.mapstruct.Mapper;
import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;
import ru.tdd.geo.application.models.dto.geo.country.CountryDetailsDTO;
import ru.tdd.geo.database.entities.Country;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 28.02.2026
 * Маппер стран
 */
@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryDTO toDto(Country country);

    List<CountryDTO> toDto(List<Country> countries);

    CountryDetailsDTO toDetailsDto(Country country);
}
