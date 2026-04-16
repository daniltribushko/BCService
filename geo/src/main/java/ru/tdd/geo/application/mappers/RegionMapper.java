package ru.tdd.geo.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.tdd.geo.application.models.dto.geo.region.RegionDTO;
import ru.tdd.geo.application.models.dto.geo.region.RegionDetailsDTO;
import ru.tdd.geo.database.entities.Region;

/**
 * @author Tribushko Danil
 * @since 14.04.2026
 * Маппер регионов
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = CountryMapper.class
)
public interface RegionMapper {

    RegionDTO toDto(Region region);

    @Mapping(target = "countryDTO", source = "country")
    RegionDetailsDTO toDetailsDto(Region region);
}
