package ru.tdd.geo.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tdd.bc.http.ValidationException;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.bc.utils.TextUtils;
import ru.tdd.geo.application.mappers.CityMapper;
import ru.tdd.geo.application.mappers.LocationMapper;
import ru.tdd.geo.application.models.dto.geo.city.*;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityAlreadyExistException;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.services.CityService;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;
import ru.tdd.geo.database.specifications.CitySpecification;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 13.01.2026
 */
@Service
@Transactional(readOnly = true)
public class CityServiceImp implements CityService {

    private final CityRepository cityRepository;

    private final RegionRepository regionRepository;

    private final CountryRepository countryRepository;

    private final CityMapper cityMapper;

    private final LocationMapper locationMapper;

    @Autowired
    public CityServiceImp(
            CityRepository cityRepository,
            RegionRepository regionRepository,
            CountryRepository countryRepository,
            CityMapper cityMapper,
            LocationMapper locationMapper
    ) {
        this.cityRepository = cityRepository;
        this.regionRepository = regionRepository;
        this.countryRepository = countryRepository;
        this.cityMapper = cityMapper;
        this.locationMapper = locationMapper;
    }

    @Override
    @Transactional
    public CityDTO create(CreateCityDTO dto) {
        String name = dto.getName();
        UUID regionId = dto.getRegionId();
        UUID countryId = dto.getCountryId();

        if (regionId == null && countryId == null)
            throw new ValidationException("Необходимо указать идентификатор региона или страны");

        Region region = regionId != null ?
                regionRepository.findById(regionId).orElseThrow(() -> new RegionByIdNotFoundException(regionId)) :
                null;

        Country country = region != null ? region.getCountry() : countryRepository.findById(countryId)
                                                                 .orElseThrow(() -> new CountryByIdNotFoundException(countryId));

        UUID regionIdForValid =  Optional.ofNullable(region)
                .map(Region::getId)
                .orElse(null);
        UUID countryIdForValid = country.getId();

        if (
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                name,
                                regionIdForValid,
                                countryIdForValid
                        )
                )
        )
            throw new CityAlreadyExistException(name, countryIdForValid, regionIdForValid);

        City city = new City(name, region, country);

        cityRepository.save(city);

        return cityMapper.toDto(city);
    }

    @Override
    @Transactional
    public CityDTO update(UUID id, UpdateCityDTO dto) {

        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityByIdNotFoundException(id));

        String name = dto.getName();
        Optional<UUID> regionIdOpt = Optional.ofNullable(dto.getRegionId());
        Optional<UUID> countryIdOpt = Optional.ofNullable(dto.getCountryId());

        String nameForUpdateValid = Optional.ofNullable(name).orElse(city.getName());
        UUID regionIdForUpdateValid = regionIdOpt
                .orElse(
                        Optional.ofNullable(city.getRegion())
                                .map(Region::getId)
                                .orElse(null)
                );
        UUID countryIdForUpdateValid = countryIdOpt.orElse(city.getCountry().getId());

        if (
                cityRepository.exists(CitySpecification.byNameRegionCityEqual(
                                nameForUpdateValid,
                                regionIdForUpdateValid,
                                countryIdForUpdateValid
                        )
                )
        )
            throw new CityAlreadyExistException(nameForUpdateValid, countryIdForUpdateValid, regionIdForUpdateValid);

        if (!TextUtils.isEmpty(name))
            city.setName(name);

        regionIdOpt.ifPresentOrElse(regionId -> {
                    Region region = regionRepository.findById(regionId).orElseThrow(() -> new RegionByIdNotFoundException(regionId));
                    city.setRegion(region);
                    city.setCountry(region.getCountry());
                },
                () -> {
                    city.setRegion(null);
                    countryIdOpt.ifPresent(countryId -> {
                                Country country = countryRepository.findById(countryId)
                                        .orElseThrow(() -> new CountryByIdNotFoundException(countryId));
                                city.setCountry(country);
                            }
                    );
                }
        );

        cityRepository.save(city);

        return cityMapper.toDto(city);
    }

    @Override
    public CityDetailsDTO getById(UUID id) {
        City city = cityRepository.findById(id).orElseThrow(() -> new CityByIdNotFoundException(id));
        CityDetailsDTO result = cityMapper.toDetailsDto(city);
        result.setLocations(city.getLocations().stream().map(locationMapper::toDto).toList());
        return result;
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        cityRepository.delete(cityRepository.findById(id).orElseThrow(() -> new CityByIdNotFoundException(id)));
    }

    @Override
    public CityListData getAll(String name, String regionName, String countryName, int page, int perPage) {
        var cityPage = cityRepository.findAll(
                CitySpecification.byNameRegionCityFullTextSearch(name, regionName, countryName),
                PageRequest.of(page, perPage, Sort.by("name"))
        );
        return CityListData.builder()
                .data(cityMapper.toDto(cityPage.toList()))
                .totalCount(cityPage.getTotalElements())
                .totalPages(cityPage.getTotalPages())
                .page(cityPage.getNumber())
                .count(cityPage.getSize())
                .build();
    }
}
