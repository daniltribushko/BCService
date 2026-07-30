package ru.tdd.bc.book.database.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tdd.bc.book.database.entities.Country;
import ru.tdd.bc.book.database.repositories.CountryRepository;
import ru.tdd.bc.book.database.service.CountryDbService;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 21.02.2026
 */
@Component
public class CountryDbServiceImp implements CountryDbService {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryDbServiceImp(
            CountryRepository countryRepository
    ) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Country getById(UUID id) {
        return countryRepository.findById(id).orElseThrow(() -> new CountryByIdNotFoundException(id));
    }
}
