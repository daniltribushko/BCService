package ru.tdd.geo.application.services.imp;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tdd.geo.application.models.dto.geo.city.*;
import ru.tdd.geo.application.models.exceptions.ValidationException;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityAlreadyExistException;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.country.CountryByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.services.CityService;
import ru.tdd.geo.application.utils.TextUtils;
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
public class CityServiceImp implements CityService {

    private final CityRepository cityRepository;

    private final RegionRepository regionRepository;

    private final CountryRepository countryRepository;

    public CityServiceImp(
            CityRepository cityRepository,
            RegionRepository regionRepository,
            CountryRepository countryRepository
    ) {
        this.cityRepository = cityRepository;
        this.regionRepository = regionRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public CityDTO create(CreateCityDTO dto) {
        String name = dto.getName();
        UUID regionId = dto.getRegionId();
        UUID countryId = dto.getCountryId();

        if (regionId == null && countryId == null)
            throw new ValidationException("Необходимо указать идентификатор региона или страны");

        Region region = regionId != null ?
                regionRepository.findById(regionId).orElseThrow(RegionByIdNotFoundException::new) :
                null;

        Country country = region != null ? region.getCountry() : countryRepository.findById(countryId)
                .orElseThrow(CountryByIdNotFoundException::new);

        if (
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                name,
                                Optional.ofNullable(region)
                                        .map(Region::getId)
                                        .orElse(null),
                                country.getId())
                )
        )
            throw new CityAlreadyExistException();

        City city = new City(name, region, country);

        cityRepository.save(city);

        return CityDTO.mapFromEntity(city);
    }

    @Override
    public CityDTO update(UUID id, UpdateCityDTO dto) {

        City city = cityRepository.findById(id)
                .orElseThrow(CityByIdNotFoundException::new);

        String name = dto.getName();
        Optional<UUID> regionIdOpt = Optional.ofNullable(dto.getRegionId());
        Optional<UUID> countryIdOpt = Optional.ofNullable(dto.getCountryId());

        if (
                cityRepository.exists(CitySpecification.byNameRegionCityEqual(
                                Optional.ofNullable(name).orElse(city.getName()),
                                regionIdOpt
                                        .orElse(
                                                Optional.ofNullable(city.getRegion())
                                                        .map(Region::getId)
                                                        .orElse(null)
                                        ),
                                countryIdOpt.orElse(city.getCountry().getId())
                        )
                )
        )
            throw new CityAlreadyExistException();

        if (!TextUtils.isEmptyWithNull(name))
            city.setName(name);

        regionIdOpt.ifPresentOrElse(regionId -> {
                    Region region = regionRepository.findById(regionId).orElseThrow(RegionByIdNotFoundException::new);
                    city.setRegion(region);
                    city.setCountry(region.getCountry());
                },
                () -> {
                    city.setRegion(null);
                    countryIdOpt.ifPresent(countryId -> {
                                Country country = countryRepository.findById(countryId).orElseThrow(CountryByIdNotFoundException::new);
                                city.setCountry(country);
                            }
                    );
                }
        );

        cityRepository.save(city);

        return CityDTO.mapFromEntity(city);
    }

    @Override
    public CityDetailsDTO getById(UUID id) {
        return CityDetailsDTO.mapFromEntity(cityRepository.findById(id).orElseThrow(CityByIdNotFoundException::new));
    }

    @Override
    public void delete(UUID id) {
        cityRepository.delete(cityRepository.findById(id).orElseThrow(CityByIdNotFoundException::new));
    }

    @Override
    public CitiesDTO getAll(String name, String regionName, String countryName, int page, int perPage) {
        return new CitiesDTO(
                cityRepository.findAll(
                                CitySpecification.byNameRegionCityFullTextSearch(name, regionName, countryName),
                                PageRequest.of(page, perPage, Sort.by("name"))
                        ).stream()
                        .map(CityDTO::mapFromEntity)
                        .toList()
        );
    }
}
