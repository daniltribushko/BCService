package ru.tdd.geo.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tdd.bc.utils.TextUtils;
import ru.tdd.geo.application.mappers.LocationMapper;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationListData;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.locations.LocationAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.locations.LocationByIdNotFoundException;
import ru.tdd.geo.application.services.LocationService;
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

    private final LocationMapper locationMapper;

    @Autowired
    public LocationServiceImp(
            LocationRepository locationRepository,
            CityRepository cityRepository,
            LocationMapper locationMapper
    ) {
        this.locationRepository = locationRepository;
        this.cityRepository = cityRepository;
        this.locationMapper = locationMapper;
    }

    @Override
    public LocationDTO create(CreateLocationDTO dto) {
        String name = dto.getName();
        UUID cityId = dto.getCity();

        if (locationRepository.exists(LocationSpecification.byNameAndCityIdEqual(name, cityId)))
            throw new LocationAlreadyExistsException(name, cityId);

        City city = cityRepository.findById(cityId).orElseThrow(() -> new CityByIdNotFoundException(cityId));

        Location location = new Location(name, city);

        locationRepository.save(location);

        return locationMapper.toDto(location);
    }

    @Override
    public LocationDTO update(UUID id, UpdateLocationDTO dto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new LocationByIdNotFoundException(id));

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
            throw new LocationAlreadyExistsException(name, cityIdOpt.orElse(location.getCity().getId()));

        if (!TextUtils.isEmpty(name))
            location.setName(name);

        cityIdOpt.ifPresent(cityId -> {
            City city = cityRepository.findById(cityId).orElseThrow(() -> new CityByIdNotFoundException(cityId));
            location.setCity(city);
        });

        locationRepository.save(location);

        return locationMapper.toDto(location);
    }

    @Override
    public LocationDTO getById(UUID id) {
        return locationMapper.toDto(locationRepository.findById(id)
                .orElseThrow(() -> new LocationByIdNotFoundException(id)));
    }

    @Override
    public void delete(UUID id) {
        locationRepository.delete(locationRepository.findById(id)
                .orElseThrow(() -> new LocationByIdNotFoundException(id)));
    }

    @Override
    public LocationListData getAll(String name, String cityName, String regionName, String countryName, int page, int perPage) {
        var locationPage = locationRepository.findAll(
                LocationSpecification.byNameAndCityNameFulltextSearch(name, cityName, regionName, countryName),
                PageRequest.of(page, perPage, Sort.by("name"))
        );

        return LocationListData.builder()
                .data(locationMapper.toDto(locationPage.toList()))
                .count(locationPage.getSize())
                .page(locationPage.getNumber())
                .totalCount(locationPage.getTotalElements())
                .totalPages(locationPage.getTotalPages())
                .build();
    }
}
