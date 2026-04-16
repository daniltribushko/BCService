package ru.tdd.geo.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.database.entities.Location;

/**
 * @author Tribushko Danil
 * @since 14.04.2026
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = CityMapper.class
)
public interface LocationMapper {

    LocationDTO toDto(Location location);

}
