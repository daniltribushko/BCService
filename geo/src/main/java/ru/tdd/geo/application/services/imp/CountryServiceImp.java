package ru.tdd.geo.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tdd.geo.application.models.dto.geo.country.*;
import ru.tdd.geo.application.models.exceptions.AlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.NotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.country.CountryAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.country.CountryByIdNotFoundException;
import ru.tdd.geo.application.services.CountryService;
import ru.tdd.geo.application.utils.TextUtils;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.specifications.NameSpecification;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 06.01.2026
 */
@Service
public class CountryServiceImp implements CountryService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryServiceImp(
            CountryRepository countryRepository
    ) {
        this.countryRepository = countryRepository;
    }

    @Override
    public CountryDTO create(CreateCountryDTO createDTO) {
        String name = createDTO.getName();
        if (countryRepository.exists(NameSpecification.byNameEqual(name))) {
            throw new CountryAlreadyExistsException();
        }
        Country country = new Country(name);
        countryRepository.save(country);
        return new CountryDTO(country.getId(), country.getName());
    }

    @Override
    public CountryDTO update(UUID id, UpdateCountryDTO updateDTO) {
        Country country = countryRepository.findById(id)
                .orElseThrow(CountryByIdNotFoundException::new);

        String newName = updateDTO.getName();

        if (!TextUtils.isEmptyWithNull(newName)) {
            if (!countryRepository.exists(NameSpecification.byNameEqual(newName))) {
                country.setName(newName);

            } else
                throw new CountryAlreadyExistsException();
        }

        countryRepository.save(country);

        return new CountryDTO(country.getId(), country.getName());
    }

    @Override
    public void delete(UUID id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(CountryByIdNotFoundException::new);

        countryRepository.delete(country);
    }

    @Override
    public CountryDetailsDTO getById(UUID id) {
        return CountryDetailsDTO.mapFromEntity(countryRepository.findById(id)
                .orElseThrow(CountryByIdNotFoundException::new));
    }

    @Override
    public CountriesDTO getAll(String name, int page, int perPage) {
        return new CountriesDTO(
                countryRepository.findAll(
                                NameSpecification.byNameWithFullTextSearch(name),
                                PageRequest.of(page, perPage, Sort.by("name"))
                        )
                        .stream()
                        .map(CountryDTO::mapFromEntity)
                        .toList()
        );
    }
}
