package ru.tdd.author.mappers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tdd.author.application.dto.countries.CountryDTO;
import ru.tdd.author.application.mappers.CountryMapper;
import ru.tdd.author.database.entitites.Country;

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

        Country country = new Country("Россия");
        country.setId(countryId);

        CountryDTO actual = countryMapper.toDto(country);

        Assertions.assertEquals(countryId, country.getId());
        Assertions.assertEquals("Россия", actual.getName());
    }
}
