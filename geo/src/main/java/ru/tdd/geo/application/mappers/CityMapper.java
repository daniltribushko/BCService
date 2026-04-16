package ru.tdd.geo.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.tdd.geo.application.models.dto.geo.city.CityDTO;
import ru.tdd.geo.application.models.dto.geo.city.CityDetailsDTO;
import ru.tdd.geo.database.entities.City;

/**
 * @author Tribushko Danil
 * @since 14.04.2026
 * Маппер городов
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CountryMapper.class, RegionMapper.class}
)
public interface CityMapper {

    CityDTO toDto(City city);

    @Mapping(target = "locations", ignore = true)
    CityDetailsDTO toDetailsDto(City city);
}
