package ru.tdd.geo.integrations.database.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.specifications.NameSpecification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribusko Danil
 * @since 03.01.2026
 * Набор тестов для репозитория страны
 */
@DataJpaTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест репозитория стран")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    void cleanBd() {
        countryRepository.deleteAll();
    }

    @Test
    @DisplayName("Удачное сохранение")
    void saveTest() {
        long expectedCount = countryRepository.count() + 1;
        countryRepository.save(
                new Country("Save Test Country")
        );
        long actualCount = countryRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteTest() {
        Country country = new Country("Delete Test Country");
        countryRepository.save(
                country
        );
        long expectedCount = countryRepository.count() - 1;
        countryRepository.delete(country);
        long actualCount = countryRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void findByIdTest() {
        Country country1 = new Country("Test Country Find By Id 1");
        Country country2 = new Country("Test Country Find By Id 2");

        countryRepository.save(
                country1
        );

        countryRepository.save(
                country2
        );

        Optional<Country> foundCountry1 = countryRepository.findById(country1.getId());
        Optional<Country> foundCountry2 = countryRepository.findById(country2.getId());

        Optional<Country> notFoundCountry1 = countryRepository.findById(UUID.randomUUID());
        Optional<Country> notFoundCountry2 = countryRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(foundCountry1.isPresent());
        Assertions.assertTrue(foundCountry2.isPresent());

        Assertions.assertEquals(country1, foundCountry1.get());
        Assertions.assertEquals(country2, foundCountry2.get());

        Assertions.assertFalse(notFoundCountry1.isPresent());
        Assertions.assertFalse(notFoundCountry2.isPresent());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateTest() {
        Country country = new Country("Test Country Update");
        countryRepository.save(country);
        country.setName("New name");
        countryRepository.save(country);

        Optional<Country> updatedCountry = countryRepository.findById(country.getId());

        Assertions.assertTrue(updatedCountry.isPresent());
        Assertions.assertEquals("New name", updatedCountry.get().getName());
    }

    @Test
    @DisplayName("Получение всех записей")
    void findAllTest() {
        Country country1 = new Country("Country Find All 1");
        Country country2 = new Country("Country Find All 2");
        Country country3 = new Country("Country Find All 3");

        countryRepository.saveAll(List.of(country1, country2, country3));

        Assertions.assertEquals(3, countryRepository.findAll().size());
    }

    @Test
    @DisplayName("Полнотекстовый поиск по названию")
    void findByNameTest() {
        Country country1 = new Country("Country");
        Country country2 = new Country("Russia");
        Country country3 = new Country("Test CoUnTrY");

        countryRepository.saveAll(List.of(country1, country2, country3));

        List<Country> countries1 = countryRepository.findAll(NameSpecification.byNameWithFullTextSearch("cOuNt"));
        List<Country> countries2 = countryRepository.findAll(NameSpecification.byNameWithFullTextSearch("u"));
        List<Country> countries3 = countryRepository.findAll(NameSpecification.byNameWithFullTextSearch("RUS"));
        List<Country> countries4 = countryRepository.findAll(NameSpecification.byNameWithFullTextSearch("testing"));

        Assertions.assertEquals(2, countries1.size());
        Assertions.assertEquals(3, countries2.size());
        Assertions.assertEquals(1, countries3.size());
        Assertions.assertEquals(0, countries4.size());
    }

    @Test
    @DisplayName("Наличие по названию")
    void existsByNameTest() {
        Country country1 = new Country("CoUntrY");
        Country country2 = new Country("Russia");

        countryRepository.saveAll(List.of(country1, country2));

        Optional<Country> foundCountry1 = countryRepository.findOne(NameSpecification.byNameEqual("country"));
        Optional<Country> foundCountry2 = countryRepository.findOne(NameSpecification.byNameEqual("rUsSiA"));
        Optional<Country> notFoundCountry1 = countryRepository.findOne(NameSpecification.byNameEqual("test"));
        Optional<Country> notFoundCountry2 = countryRepository.findOne(NameSpecification.byNameEqual("C"));

        Assertions.assertTrue(foundCountry1.isPresent());
        Assertions.assertTrue(foundCountry2.isPresent());
        Assertions.assertEquals(country1, foundCountry1.get());
        Assertions.assertEquals(country2, foundCountry2.get());

        Assertions.assertFalse(notFoundCountry1.isPresent());
        Assertions.assertFalse(notFoundCountry2.isPresent());
    }
}
