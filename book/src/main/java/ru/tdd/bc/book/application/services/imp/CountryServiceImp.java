package ru.tdd.bc.book.application.services.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.dto.countries.CountryListDTO;
import ru.tdd.bc.book.application.mappers.CountryMapper;
import ru.tdd.bc.book.application.services.CountryService;
import ru.tdd.bc.book.database.entities.Country;
import ru.tdd.bc.book.database.repositories.CountryRepository;
import ru.tdd.bc.database.specifications.NameSpecification;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
@Service
@Transactional(readOnly = true)
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
        Page<Country> countryPage = countryRepository.findAll(
                NameSpecification.byNameWithFullTextSearch(name),
                PageRequest.of(page, perPage)
        );
        return CountryListDTO.builder()
                .data(countryMapper.toDto(countryPage.toList()))
                .totalCount(countryPage.getTotalElements())
                .totalPages(countryPage.getTotalPages())
                .page(countryPage.getNumber())
                .count(countryPage.getSize())
                .build();
    }

    @Override
    public CountryDTO getById(UUID id) {
        return countryMapper.toDto(
                countryRepository.findById(id)
                        .orElseThrow(() -> new CountryByIdNotFoundException(id))
        );
    }
}

