package ru.tdd.author.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.application.dto.countries.CountryListDTO;
import ru.tdd.author.application.exceptions.country.CountryByIdNotFoundException;
import ru.tdd.author.application.mappers.CountryMapper;
import ru.tdd.author.application.services.CountryService;
import ru.tdd.author.database.repositories.CountryRepository;
import ru.tdd.author.database.specifications.CountrySpecification;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
@Service
public class CountryServiceImp implements CountryService {

    private final CountryRepository countryRepository;

    private final CountryMapper countryMapper;

    @Autowired
    public CountryServiceImp(
            CountryRepository countryRepository,
            CountryMapper countryMapper
    ) {
        this.countryRepository = countryRepository;
        this.countryMapper = countryMapper;
    }

    @Override
    public CountryListDTO getAll(String name, int page, int perPage) {
        return new CountryListDTO(
                countryRepository.findAll(
                                CountrySpecification.byNameLike(name),
                                PageRequest.of(page, perPage)
                        )
                        .stream()
                        .map(countryMapper::toDto)
                        .toList()
        );
    }

    @Override
    public CountryDTO getById(UUID id) {
        return countryMapper.toDto(
                countryRepository.findById(id)
                        .orElseThrow(() -> new CountryByIdNotFoundException(id))
        );
    }
}
