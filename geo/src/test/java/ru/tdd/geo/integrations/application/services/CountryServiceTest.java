package ru.tdd.geo.integrations.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.country.*;
import ru.tdd.geo.application.models.exceptions.AlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.NotFoundException;
import ru.tdd.geo.application.services.CountryService;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.repositories.CountryRepository;

import java.time.ZoneId;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Набор интеграционных тестов сервиса по работе со странами
 */
@SpringBootTest
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryServiceTest {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryService countryService;

    @BeforeEach
    void cleanDb() {
        countryRepository.deleteAll();
    }

    @Test
    @Transactional
    void saveSuccessTest() {
        long expectedCount = countryRepository.count() + 1;
        CountryDTO countryDTO = countryService.create(new CreateCountryDTO("New Country", ZoneId.systemDefault()));
        long actualCount = countryRepository.count();

        Assertions.assertEquals("New Country", countryDTO.getName());
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    @Transactional
    void saveAlreadyExistsFailTest() {
        countryRepository.save(new Country("Already Exists Country"));

        AlreadyExistsException actual = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> countryService.create(new CreateCountryDTO("Already Exists Country", ZoneId.systemDefault()))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным названием уже создана", actual.getMessage());
    }

    @Test
    @Transactional
    void updateSuccessTest() {
        Country country1 = new Country("Country For Update");
        Country country2 = new Country("Country For Update Time Zone");
        Country country3 = new Country("Country For Update With Null Dto");

        countryRepository.save(country1);
        countryRepository.save(country2);
        countryRepository.save(country3);

        CountryDTO actualUpdateName = countryService.update(
                country1.getId(),
                new UpdateCountryDTO("New Country Name", null)
        );

        CountryDTO actualUpdateZoneId = countryService.update(
                country2.getId(),
                new UpdateCountryDTO(null, ZoneId.of("Europe/Moscow"))
        );

        CountryDTO actualWithNulls = countryService.update(
                country3.getId(),
                new UpdateCountryDTO(null, null)
        );

        Assertions.assertEquals(country1.getId(), actualUpdateName.getId());
        Assertions.assertEquals("New Country Name", actualUpdateName.getName());
        Assertions.assertEquals(country1.getZoneId(), actualUpdateName.getZoneId());

        Assertions.assertEquals(country2.getId(), actualUpdateZoneId.getId());
        Assertions.assertEquals("Country For Update Time Zone", country2.getName());
        Assertions.assertEquals(ZoneId.of("Europe/Moscow"), country2.getZoneId());

        Assertions.assertEquals(country3.getId(), actualWithNulls.getId());
        Assertions.assertEquals(country3.getName(), actualWithNulls.getName());
        Assertions.assertEquals(country3.getZoneId(), actualWithNulls.getZoneId());
    }

    @Test
    @Transactional
    void updateAlreadyExistsFail() {
        Country country1 = new Country("Already Exists Country");
        Country country2 = new Country("Country For Fail Update");

        countryRepository.save(country1);
        countryRepository.save(country2);

        AlreadyExistsException actual = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> countryService.update(country2.getId(), new UpdateCountryDTO("Already Exists Country", null))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным названием уже создана", actual.getMessage());
    }

    @Test
    @Transactional
    void deleteSuccessTest() {
        Country country = new Country("Country For Delete");
        countryRepository.save(country);
        long expectedCount = countryRepository.count() - 1;
        countryService.delete(country.getId());
        long actualCount = countryRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    @Transactional
    void deleteNotFoundFailTest() {
        NotFoundException actual = Assertions.assertThrows(
                NotFoundException.class,
                () -> countryService.delete(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    @Transactional
    void findByIdSuccessTest() {
        Country country = new Country("Find By Id Test Country");
        countryRepository.save(country);

        CountryDetailsDTO actual = countryService.getById(country.getId());

        Assertions.assertEquals(country.getId(), actual.getId());
        Assertions.assertEquals("Find By Id Test Country", actual.getName());
    }

    @Test
    @Transactional
    void findByIdNotFoundFailTest() {
        NotFoundException actual = Assertions.assertThrows(
                NotFoundException.class,
                () -> countryService.getById(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void findAllTest() {
        Country country1 = new Country("Russia");
        Country country2 = new Country("USA");
        Country country3 = new Country("China");
        Country country4 = new Country("Rus");
        Country country5 = new Country("UK");

        countryRepository.save(country1);
        countryRepository.save(country2);
        countryRepository.save(country3);
        countryRepository.save(country4);
        countryRepository.save(country5);

        CountriesDTO countries1 = countryService.getAll("Us", 1, 2);
        CountriesDTO countries2 = countryService.getAll("cHIn", 1, 1);
        CountriesDTO countries3 = countryService.getAll("u", 1, 2);
        CountriesDTO countries4 = countryService.getAll(null, 0, 100);
        CountriesDTO countries5 = countryService.getAll("", 0, 100);

        Assertions.assertEquals(1, countries1.getData().size());
        Assertions.assertEquals(0, countries2.getData().size());
        Assertions.assertEquals(2, countries3.getData().size());
        Assertions.assertEquals(5, countries4.getData().size());
        Assertions.assertEquals(5, countries5.getData().size());
    }
}
