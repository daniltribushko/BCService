
package ru.tdd.geo.integrations.database.repositories;

import org.junit.jupiter.api.Assertions;
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
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.specifications.CitySpecification;
import ru.tdd.geo.sql.InitCitiesSqlScrips;
import ru.tdd.geo.utils.CityUtils;
import ru.tdd.geo.utils.CountryUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 02.01.2026
 * Набор тестов репозитория городов
 */

@DataJpaTest
@Testcontainers
@InitCitiesSqlScrips
@DisplayName("Интеграционный тест репозитория городов")
@Import(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @DisplayName("Удачное сохранение")
    void saveTest() {
        Country country = countryRepository.findById(CountryUtils.COUNTRY_ID1).get();

        City city = new City("Сургут", null, country);

        cityRepository.save(city);
        long actualCount = cityRepository.count();

        Assertions.assertEquals(12, actualCount);
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteTest() {
        City city = cityRepository.getReferenceById(CityUtils.CITY_ID6);

        cityRepository.delete(city);
        long actualCount = cityRepository.count();

        Assertions.assertEquals(10, actualCount);
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void findByIdTest() {
        Optional<City> foundCity1 = cityRepository.findById(CityUtils.CITY_ID6);
        Optional<City> foundCity2 = cityRepository.findById(CityUtils.CITY_ID5);

        Optional<City> notFoundCity1 = cityRepository.findById(UUID.randomUUID());
        Optional<City> notFoundCity2 = cityRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(foundCity1.isPresent());
        Assertions.assertTrue(foundCity2.isPresent());

        Assertions.assertEquals(CityUtils.CITY_ID6, foundCity1.get().getId());
        Assertions.assertEquals(CityUtils.CITY_ID5, foundCity2.get().getId());

        Assertions.assertFalse(notFoundCity1.isPresent());
        Assertions.assertFalse(notFoundCity2.isPresent());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateTest() {
        City city = cityRepository.getReferenceById(CityUtils.CITY_ID7);
        city.setName("Свердловск");
        cityRepository.save(city);

        Optional<City> updatedCity = cityRepository.findById(city.getId());

        Assertions.assertTrue(updatedCity.isPresent());
        Assertions.assertEquals("Свердловск", updatedCity.get().getName());
    }

    @Test
    @DisplayName("Получение всех записей")
    void findAllTest() {
        Assertions.assertEquals(11, cityRepository.findAll().size());
    }

    private static Stream<Arguments> findByNameAndRegionAndCityTest() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "Ин"), null, null, 5)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Поиск по названию города, региона, страны")
    void findByNameAndRegionAndCityTest(String cityName, String regionName, String countryName, long expectedSize) {
        List<City> cities = cityRepository.findAll(CitySpecification.byNameRegionCityFullTextSearch(cityName, regionName, countryName));

        Assertions.assertEquals(expectedSize, cities.size());
    }
}
