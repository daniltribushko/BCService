package ru.tdd.geo.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationsDTO;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.locations.LocationAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.locations.LocationByIdNotFoundException;
import ru.tdd.geo.application.services.LocationService;
import ru.tdd.geo.application.utils.TextUtils;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Location;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.LocationRepository;
import ru.tdd.geo.database.specifications.LocationSpecification;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 24.01.2026
 */
@Service
public class LocationServiceImp implements LocationService {

    private final LocationRepository locationRepository;

    private final CityRepository cityRepository;

    @Autowired
    public LocationServiceImp(
            LocationRepository locationRepository,
            CityRepository cityRepository
    ) {
        this.locationRepository = locationRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    public LocationDTO create(CreateLocationDTO dto) {
        String name = dto.getName();
        UUID cityId = dto.getCity();

        if (locationRepository.exists(LocationSpecification.byNameAndCityIdEqual(name, cityId)))
            throw new LocationAlreadyExistsException();

        City city = cityRepository.findById(cityId).orElseThrow(CityByIdNotFoundException::new);

        Location location = new Location(name, city);

        locationRepository.save(location);

        return LocationDTO.mapFromEntity(location);
    }

    @Override
    public LocationDTO update(UUID id, UpdateLocationDTO dto) {
        Location location = locationRepository.findById(id).orElseThrow(LocationByIdNotFoundException::new);

        String name = dto.getName();
        Optional<UUID> cityIdOpt = Optional.ofNullable(dto.getCityId());

        if (
                locationRepository.exists(
                        LocationSpecification.byNameAndCityIdEqual(
                                Optional.ofNullable(name).isEmpty() ? location.getName() : name,
                                cityIdOpt.orElse(location.getCity().getId())
                        )
                )
        )
            throw new LocationAlreadyExistsException();

        if (!TextUtils.isEmptyWithNull(name))
            location.setName(name);

        cityIdOpt.ifPresent(cityId -> {
            City city = cityRepository.findById(cityId).orElseThrow(CityByIdNotFoundException::new);
            location.setCity(city);
        });

        locationRepository.save(location);

        return LocationDTO.mapFromEntity(location);
    }

    @Override
    public LocationDTO getById(UUID id) {
        return LocationDTO.mapFromEntity(locationRepository.findById(id).orElseThrow(LocationByIdNotFoundException::new));
    }

    @Override
    public void delete(UUID id) {
        locationRepository.delete(locationRepository.findById(id).orElseThrow(LocationByIdNotFoundException::new));
    }

    @Override
    public LocationsDTO getAll(String name, String cityName, int page, int perPage) {
        return new LocationsDTO(
                locationRepository.findAll(
                        LocationSpecification.byNameAndCityNameFulltextSearch(name, cityName),
                        PageRequest.of(page, perPage)
                )
                        .stream()
                        .map(LocationDTO::mapFromEntity)
                        .toList()
        );
    }
}
