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
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;
import ru.tdd.geo.database.specifications.RegionSpecification;
import ru.tdd.geo.sql.InitRegionsSqlScripts;
import ru.tdd.geo.utils.CountryUtils;
import ru.tdd.geo.utils.RegionUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 04.01.2026
 * Набор тестов для репозитория регионов
 */
@DataJpaTest
@Testcontainers
@InitRegionsSqlScripts
@DisplayName("Тест репозитория регионов")
@Import(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RegionRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Test
    @DisplayName("Удачное сохранение")
    void saveTest() {
        Country country = countryRepository.getReferenceById(CountryUtils.COUNTRY_ID1);
        regionRepository.save(new Region("Брянская область", country));
        long actualCount = regionRepository.count();

        Assertions.assertEquals(7, actualCount);
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteTest() {
        Region region = regionRepository.getReferenceById(RegionUtils.REGION_ID4);
        regionRepository.delete(region);
        long actualCount = regionRepository.count();

        Assertions.assertEquals(5, actualCount);
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void findByIdTest() {
        Optional<Region> foundRegion1 = regionRepository.findById(RegionUtils.REGION_ID3);
        Optional<Region> foundRegion2 = regionRepository.findById(RegionUtils.REGION_ID6);

        Optional<Region> notFoundRegion1 = regionRepository.findById(UUID.randomUUID());
        Optional<Region> notFoundRegion2 = regionRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(foundRegion1.isPresent());
        Assertions.assertTrue(foundRegion2.isPresent());

        Assertions.assertEquals(RegionUtils.REGION_ID3, foundRegion1.get().getId());
        Assertions.assertEquals(RegionUtils.REGION_ID6, foundRegion2.get().getId());

        Assertions.assertFalse(notFoundRegion1.isPresent());
        Assertions.assertFalse(notFoundRegion2.isPresent());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateTest() {
        Region region = regionRepository.getReferenceById(RegionUtils.REGION_ID3);
        region.setName("Брянская область");

        regionRepository.save(region);

        Optional<Region> updatedRegion = regionRepository.findById(region.getId());

        Assertions.assertTrue(updatedRegion.isPresent());
        Assertions.assertEquals("Брянская область", region.getName());
    }

    @Test
    @DisplayName("Получение всех записей")
    void findAllTest() {
        Assertions.assertEquals(6, regionRepository.findAll().size());
    }

    private static Stream<Arguments> findWithFiltersTest() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "ЛаСтЬ"), null, 0, 10, 3),
                arguments(named("Поиск по названию 2", "аньхой"), null, 0, 10, 1),
                arguments(named("Поиск по названию страны 1", null), "Китай", 0, 10, 2),
                arguments(named("Поиск по названию региона 2", null), "РОС", 0, 10, 3),
                arguments(named("Поиск по названию региона и страны 1", "АНЬ"), "ай", 0, 10, 1),
                arguments(named("Поиск по названию региона и страны 2", "иЛи"), "ИТ", 0, 10, 1),
                arguments(named("Поиск без названий", null), null, 0, 10, 6),
                arguments(named("Поиск с пустыми названиями", ""), "", 0, 10, 6),
                arguments(named("Пагинация 1", null), null, 4, 1, 1),
                arguments(named("Пагинация 2", null), null, 1, 3, 3)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Полнотекстовый поиск по названию и стране")
    void findWithFiltersTest(String name, String countryName, int page, int perPage, long expectedSize) {
        List<Region> regions = regionRepository.findAll(
                RegionSpecification.byNameAndCountryNameFullTextSearch(name, countryName),
                PageRequest.of(page, perPage)
        ).getContent();

        Assertions.assertEquals(expectedSize, regions.size());
    }

    private static Stream<Arguments> existsByNameAndCountryTest() {
        return Stream.of(
                arguments(named("Наличие 1", "Московская область"), CountryUtils.COUNTRY_ID1, true),
                arguments(named("Наличие 2", "АНЬХОЙ"), CountryUtils.COUNTRY_ID2, true),
                arguments(named("Отсутствие 1", "Брянская область"), CountryUtils.COUNTRY_ID1, false),
                arguments(named("Отсутсвие 2", "аньхой"), UUID.randomUUID(), false)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Наличие по названию и стране")
    void existsByNameAndCountryTest(String name, UUID countryId, boolean isExists) {
        Assertions.assertEquals(isExists, regionRepository.exists(RegionSpecification.byNameAndCountryIdEqual(name, countryId)));
    }
}
