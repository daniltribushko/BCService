package ru.tdd.geo.integrations.database.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.database.specifications.NameSpecification;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.sql.InitCountriesSqlScripts;
import ru.tdd.geo.utils.CountryUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribusko Danil
 * @since 03.01.2026
 * Набор тестов для репозитория страны
 */
@DataJpaTest
@Testcontainers
@InitCountriesSqlScripts
@Import(TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест репозитория стран")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @DisplayName("Удачное сохранение")
    void saveTest() {
        countryRepository.save(new Country("Сербия"));
        long actualCount = countryRepository.count();

        Assertions.assertEquals(5, actualCount);
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteTest() {
        Country country = countryRepository.getReferenceById(CountryUtils.COUNTRY_ID3);

        countryRepository.delete(country);
        long actualCount = countryRepository.count();

        Assertions.assertEquals(3, actualCount);
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void findByIdTest() {
        Optional<Country> foundCountry1 = countryRepository.findById(CountryUtils.COUNTRY_ID1);
        Optional<Country> foundCountry2 = countryRepository.findById(CountryUtils.COUNTRY_ID2);

        Optional<Country> notFoundCountry1 = countryRepository.findById(UUID.randomUUID());
        Optional<Country> notFoundCountry2 = countryRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(foundCountry1.isPresent());
        Assertions.assertTrue(foundCountry2.isPresent());

        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, foundCountry1.get().getId());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID2, foundCountry2.get().getId());

        Assertions.assertFalse(notFoundCountry1.isPresent());
        Assertions.assertFalse(notFoundCountry2.isPresent());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateTest() {
        Country country = countryRepository.getReferenceById(CountryUtils.COUNTRY_ID1);
        country.setName("СССР");
        countryRepository.save(country);

        Optional<Country> updatedCountry = countryRepository.findById(CountryUtils.COUNTRY_ID1);

        Assertions.assertTrue(updatedCountry.isPresent());
        Assertions.assertEquals("СССР", updatedCountry.get().getName());
    }

    @Test
    @DisplayName("Получение всех записей")
    void findAllTest() {
        Assertions.assertEquals(4, countryRepository.findAll().size());
    }

    private static Stream<Arguments> findByNameTest() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "Ия"), 3),
                arguments(named("Поиск по названию 2", "иТа"), 2),
                arguments(named("Поиск по названию 3", "рОсСиЯ"), 1),
                arguments(named("Поиск с пустым названием", ""), 4),
                arguments(named("Поиск без названия", null), 4),
                arguments(named("Поиск с отсуствующим названием", "Испания"), 0)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Полнотекстовый поиск стран")
    void findByNameTest(String text, long expectedSize) {

        List<Country> countries = countryRepository.findAll(NameSpecification.byNameWithFullTextSearch(text));

        Assertions.assertEquals(expectedSize, countries.size());
    }

    private static Stream<Arguments> existsByNameTest() {
        return Stream.of(
                arguments(named("Название 1", "РОССИЯ"), true),
                arguments(named("Название 2", "КиТаЙ"), true),
                arguments(named("Отсуствующее значение 1", "Польша"), false),
                arguments(named("Отсуствующее значение 2", "Германия"), false)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Наличие по названию")
    void existsByNameTest(String name, boolean expected) {
        boolean actual = countryRepository.exists(NameSpecification.byNameEqual(name));

        Assertions.assertEquals(expected, actual);
    }
}
