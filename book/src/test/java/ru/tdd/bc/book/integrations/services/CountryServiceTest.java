package ru.tdd.bc.book.integrations.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.book.TestcontainersConfiguration;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.services.CountryService;
import ru.tdd.bc.book.sql.InitCountriesSqlScripts;
import ru.tdd.bc.book.utils.CountryUtils;

@SpringBootTest
@Testcontainers
@InitCountriesSqlScripts
@DisplayName("Интеграционный тест сервиса стран")
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryServiceTest {

    @Autowired
    private CountryService countryService;

    @Test
    @DisplayName("Получение страны по идентификатору")
    void getByIdSuccessTest() {

        CountryDTO actual = countryService.getById(CountryUtils.COUNTRY_ID2);

        Assertions.assertEquals(CountryUtils.COUNTRY_ID2, actual.getId());
    }
}

