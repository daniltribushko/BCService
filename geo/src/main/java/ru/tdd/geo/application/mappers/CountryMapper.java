package ru.tdd.geo.application.mappers;

import org.mapstruct.Mapper;
import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;
import ru.tdd.geo.database.entities.Country;

/**
 * @author Tribushko Danil
 * @since 28.02.2026
 * Маппер стран
 */
@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryDTO toDto(Country country);

    Country toEntity(CountryDTO dto);
}
