package ru.tdd.author.integrations.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.author.TestcontainersConfiguration;
import ru.tdd.author.database.entitites.Country;
import ru.tdd.author.database.repositories.CountryRepository;
import ru.tdd.author.database.specifications.CountrySpecification;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 2026.02.19
 * Набор тестов для репозитория по работе со странами
 */
@DataJpaTest
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Тестирование репозитория стран")
class CountryRepositoryTest {

    private final CountryRepository countryRepository;

    @Autowired
    CountryRepositoryTest(
            CountryRepository countryRepository
    ) {
        this.countryRepository = countryRepository;
    }

    @BeforeEach
    void cleanDb() {
        countryRepository.deleteAll();
    }

    @Test
    @DisplayName("Поиск по названию")
    void findByNameTest() {
        Country country1 = new Country("Россия");
        Country country2 = new Country("Руанда");
        Country country3 = new Country("Китай");

        countryRepository.saveAll(List.of(country1, country2, country3));

        Assertions.assertEquals(2, countryRepository.findAll(CountrySpecification.byNameLike("р")).size());
        Assertions.assertEquals(1, countryRepository.findAll(CountrySpecification.byNameLike("КитАй")).size());
    }

    @Test
    @DisplayName("Поиск по пустому названию")
    void findByEmptyNameTest() {
        Country country1 = new Country("Россия");
        Country country2 = new Country("Руанда");
        Country country3 = new Country("Китай");

        countryRepository.saveAll(List.of(country1, country2, country3));

        Assertions.assertEquals(3, countryRepository.findAll(CountrySpecification.byNameLike(null)).size());
        Assertions.assertEquals(3, countryRepository.findAll(CountrySpecification.byNameLike("")).size());
    }
}
