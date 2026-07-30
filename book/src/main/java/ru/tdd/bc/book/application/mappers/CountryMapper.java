package ru.tdd.bc.book.application.mappers;

import org.mapstruct.Mapper;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.database.entities.Country;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 19.02.2026
 * Маппер стран
 */
@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryDTO toDto(Country country);

    List<CountryDTO> toDto(List<Country> countries);

    Country toEntity(CountryDTO countryDTO);
}
