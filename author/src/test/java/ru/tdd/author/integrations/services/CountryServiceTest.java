package ru.tdd.author.integrations.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.author.TestcontainersConfiguration;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.application.services.CountryService;
import ru.tdd.author.database.entitites.Country;
import ru.tdd.author.database.repositories.CountryRepository;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@DisplayName(value = "Интеграционные тесты сервиса по работе со странами")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryServiceTest {

    private final CountryRepository countryRepository;

    private final CountryService countryService;

    @Autowired
    public CountryServiceTest(
            CountryRepository countryRepository,
            CountryService countryService
    ) {
        this.countryRepository = countryRepository;
        this.countryService = countryService;
    }

    @BeforeEach
    void cleanDb() {
        countryRepository.deleteAll();
    }

    @Test
    @DisplayName("Получение страны по идентификатору")
    void getByIdSuccessTest() {
        Country country1 = new Country("Россия");
        Country country2 = new Country("Китай");

        country1.setId(UUID.randomUUID());
        country2.setId(UUID.randomUUID());

        countryRepository.saveAll(List.of(country1, country2));

        CountryDTO actual = countryService.getById(country2.getId());

        Assertions.assertEquals(actual.getId(), country2.getId());
    }
}
