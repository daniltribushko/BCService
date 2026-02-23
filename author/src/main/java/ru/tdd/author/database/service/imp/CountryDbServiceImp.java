package ru.tdd.author.database.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tdd.author.application.exceptions.country.CountryByIdNotFoundException;
import ru.tdd.author.database.entitites.Country;
import ru.tdd.author.database.repositories.CountryRepository;
import ru.tdd.author.database.service.CountryDbService;

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

    @Override
    public void checkCountryExists(UUID id) {
        if (!countryRepository.existsById(id))
            throw new CountryByIdNotFoundException(id);
    }
}
