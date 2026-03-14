package ru.tdd.author.application.mappers;

import org.mapstruct.Mapper;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.database.entitites.Country;

/**
 * @author Tribushko Danil
 * @since 19.02.2026
 * Маппер стран
 */
@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryDTO toDto(Country country);

    Country toEntity(CountryDTO countryDTO);
}
