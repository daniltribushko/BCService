package ru.tdd.bc.book.integrations.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.mappers.CountryMapperImpl;
import ru.tdd.bc.book.database.entities.Country;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 * Набор тестов маппера стран
 */
@DisplayName("Тестирование маппера стран")
class CountryMapperTest {

    private CountryMapperImpl countryMapper;

    @BeforeEach
    void setMapper() {
        countryMapper = new CountryMapperImpl();
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

