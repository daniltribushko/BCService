package ru.tdd.bc.book.integrations.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.mappers.CountryMapper;
import ru.tdd.bc.book.database.entities.Country;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 * Набор тестов маппера стран
 */
@SpringBootTest
@DisplayName("Тестирование маппера стран")
class CountryMapperTest {

    private CountryMapper countryMapper;

    @Autowired
    public CountryMapperTest(CountryMapper countryMapper) {
        this.countryMapper = countryMapper;
    }

    @Test
    @DisplayName("Преобразование в dto")
    void mapCountryTest() {
        UUID countryId = UUID.randomUUID();

        Country country = new Country(countryId, "Россия");
        CountryDTO actual = countryMapper.toDto(country);

        Assertions.assertEquals(countryId, country.getId());
        Assertions.assertEquals("Россия", actual.getName());
    }
}

