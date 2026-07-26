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
import org.springframework.data.domain.PageRequest;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Location;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.LocationRepository;
import ru.tdd.geo.database.specifications.LocationSpecification;
import ru.tdd.geo.sql.InitLocationsSqlScrips;
import ru.tdd.geo.utils.CityUtils;
import ru.tdd.geo.utils.LocationUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 02.01.2026
 * Набор тестов репозитория локаций
 */
@DataJpaTest
@Testcontainers
@InitLocationsSqlScrips
@DisplayName("Тест репозитория локаций")
@Import(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CityRepository cityRepository;

    @Test
    @DisplayName("Удачное сохранение")
    void saveTest() {
        City city = cityRepository.getReferenceById(CityUtils.CITY_ID7);
        Location location = new Location("УРФУ", city);

        locationRepository.save(location);
        long actualCount = locationRepository.count();

        Assertions.assertEquals(7, actualCount);
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteTest() {
        Location location = locationRepository.getReferenceById(LocationUtils.LOCATION_ID6);
        locationRepository.delete(location);
        long actualCount = locationRepository.count();

        Assertions.assertEquals(5, actualCount);
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void findByIdTest() {
        Optional<Location> foundLocation1 = locationRepository.findById(LocationUtils.LOCATION_ID2);
        Optional<Location> foundLocation2 = locationRepository.findById(LocationUtils.LOCATION_ID5);

        Optional<Location> notFoundLocation1 = locationRepository.findById(UUID.randomUUID());
        Optional<Location> notFoundLocation2 = locationRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(foundLocation1.isPresent());
        Assertions.assertTrue(foundLocation2.isPresent());

        Assertions.assertEquals(LocationUtils.LOCATION_ID2, foundLocation1.get().getId());
        Assertions.assertEquals(LocationUtils.LOCATION_ID5, foundLocation2.get().getId());

        Assertions.assertFalse(notFoundLocation1.isPresent());
        Assertions.assertFalse(notFoundLocation2.isPresent());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateTest() {
        Location location = locationRepository.getReferenceById(LocationUtils.LOCATION_ID2);
        location.setName("Мавзолей");

        locationRepository.save(location);

        Optional<Location> updatedLocation = locationRepository.findById(location.getId());

        Assertions.assertTrue(updatedLocation.isPresent());
        Assertions.assertEquals("Мавзолей", updatedLocation.get().getName());
    }

    @Test
    @DisplayName("Получение всех записей")
    void findAllTest() {
        Assertions.assertEquals(6, locationRepository.findAll().size());
    }

    private static Stream<Arguments> findAllWithFiltersTest() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "ПЛОЩАДЬ"), null, null, null, 0, 10, 2),
                arguments(named("Поиск по названию 2", "таж"), null, null, null, 0, 10, 1),
                arguments(named("Поиск по названию города 1", null), "мОсКвА", null, null, 0, 10, 2),
                arguments(named("Поиск по названию города 2", null), "САНКТ-ПЕТЕРБУРГ", null, null, 0, 10, 3),
                arguments(named("Поиск по названию региона", null), null, "Московская область", null, 0, 10, 0),
                arguments(named("Поиск по названию страны 1", null), null, null, "россия", 0, 10, 5),
                arguments(named("Поиск по названию страны 2", null), null, null, "ИтАлИя", 0, 10, 1),
                arguments(named("Поиск со всеми фильтрами", "САД"), "СКва", null, "РОС", 0, 10, 1),
                arguments(named("Поиск без названий", null), null, null, null, 0, 10, 6),
                arguments(named("Поиск с пустыми названиями", ""), "", "", "", 0, 10, 6),
                arguments(named("Пагинация 1", null), null, null, null, 1, 3, 3),
                arguments(named("Пагинация 2", null), null, null, null, 0, 5, 5)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Полнотекстовый поиск по названию и городу")
    void findAllWithFiltersTest(String name, String cityName, String regionName, String countryName, int page, int perPage, long expectedSize) {
        List<Location> locations = locationRepository.findAll(
                LocationSpecification.byNameAndCityNameFulltextSearch(name, cityName, regionName, countryName),
                PageRequest.of(page, perPage)
        ).getContent();

        Assertions.assertEquals(expectedSize, locations.size());
    }

    private static Stream<Arguments> existsTest() {
        return Stream.of(
                arguments(named("Наличие 1", "Эрмитаж"), CityUtils.CITY_ID5, true),
                arguments(named("Наличие 2", "АдМиРаЛтЕйСтВо"), CityUtils.CITY_ID5, true),
                arguments(named("Отсуствующее 1", "Мавзолей"), CityUtils.CITY_ID4, false),
                arguments(named("Отсуствующее 2", "ЭРМИТАЖ"), UUID.randomUUID(), false)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Наличие по названию и городу")
    void existsTest(String name, UUID countryId, boolean isExists) {
        Assertions.assertEquals(isExists, locationRepository.exists(LocationSpecification.byNameAndCityIdEqual(name, countryId)));
    }
}
