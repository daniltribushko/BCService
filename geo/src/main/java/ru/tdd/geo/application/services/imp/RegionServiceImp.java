package ru.tdd.geo.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.bc.utils.TextUtils;
import ru.tdd.geo.application.mappers.RegionMapper;
import ru.tdd.geo.application.models.dto.geo.region.*;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.services.RegionService;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;
import ru.tdd.geo.database.specifications.RegionSpecification;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 */
@Service
public class RegionServiceImp implements RegionService {

    private final RegionRepository regionRepository;

    private final CountryRepository countryRepository;

    private final RegionMapper regionMapper;

    @Autowired
    public RegionServiceImp(
            RegionRepository regionRepository,
            CountryRepository countryRepository,
            RegionMapper regionMapper
    ) {
        this.regionRepository = regionRepository;
        this.countryRepository = countryRepository;
        this.regionMapper = regionMapper;
    }

    @Override
    public RegionDTO create(CreateRegionDTO dto) {
        String name = dto.getName();
        UUID countryId = dto.getCountryId();

        if (regionRepository.exists(RegionSpecification.byNameAndCountryIdEqual(name, countryId)))
            throw new RegionAlreadyExistsException(name, countryId);

        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new CountryByIdNotFoundException(countryId));

        Region region = new Region(name, country);

        regionRepository.save(region);

        return regionMapper.toDto(region);
    }

    @Override
    public RegionDTO update(UUID id, UpdateRegionDTO dto) {

        String name = dto.getName();
        UUID countryId = dto.getCountryId();

        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new RegionByIdNotFoundException(id));

        String newName = TextUtils.isEmpty(name) ? region.getName() : name;
        UUID newCountryId = Optional.ofNullable(countryId).orElseGet(() -> region.getCountry().getId());

        if (regionRepository.exists(RegionSpecification.byNameAndCountryIdEqual(
                newName, newCountryId)))
            throw new RegionAlreadyExistsException(name, newCountryId);

        region.setName(newName);


        if (countryId != null) {
            region.setCountry(countryRepository.findById(countryId)
                    .orElseThrow(() -> new CountryByIdNotFoundException(countryId)));
        }

        regionRepository.save(region);

        return regionMapper.toDto(region);
    }

    @Override
    public void delete(UUID id) {
        regionRepository.delete(
                regionRepository.findById(id)
                        .orElseThrow(() -> new RegionByIdNotFoundException(id))
        );
    }

    @Override
    public RegionDetailsDTO getById(UUID id) {
        return regionMapper.toDetailsDto(regionRepository.findById(id).orElseThrow(() -> new RegionByIdNotFoundException(id)));
    }

    @Override
    public RegionListData getAll(String name, String countryName, int page, int perPage) {
        var regionPage =  regionRepository.findAll(
                RegionSpecification.byNameAndCountryNameFullTextSearch(name, countryName),
                PageRequest.of(page, perPage, Sort.by("name"))
        );

        return RegionListData.builder()
                .data(regionMapper.toDto(regionPage.toList()))
                .totalCount(regionPage.getTotalElements())
                .totalPages(regionPage.getTotalPages())
                .count(regionPage.getSize())
                .page(regionPage.getNumber())
                .build();
    }
}
