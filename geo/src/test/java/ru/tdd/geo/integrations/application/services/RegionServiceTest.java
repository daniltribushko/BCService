package ru.tdd.geo.integrations.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.region.*;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.services.RegionService;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;
import ru.tdd.geo.sql.InitRegionsSqlScripts;
import ru.tdd.geo.utils.CountryUtils;
import ru.tdd.geo.utils.RegionUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 09.01.2026
 * Набор интеграционных тестов сервиса по работе с регионами
 */
@SpringBootTest
@Testcontainers
@InitRegionsSqlScripts
@Import(value = TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест сервиса регионов")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RegionServiceTest {

    private final RegionRepository regionRepository;

    private final RegionService regionService;

    @Autowired
    RegionServiceTest(RegionRepository regionRepository, RegionService regionService) {
        this.regionRepository = regionRepository;
        this.regionService = regionService;
    }

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        RegionDTO actualDto = regionService.create(new CreateRegionDTO("Брянская область", CountryUtils.COUNTRY_ID1));
        long actualCount = regionRepository.count();

        Assertions.assertEquals(7, actualCount);
        Assertions.assertEquals("Брянская область", actualDto.getName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, actualDto.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное создание - регион уже создан")
    void saveAlreadyExistsExceptionTest() {
        RegionAlreadyExistsException actual = Assertions.assertThrows(
                RegionAlreadyExistsException.class,
                () -> regionService.create(
                        new CreateRegionDTO("Московская область", CountryUtils.COUNTRY_ID1)
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(RegionAlreadyExistsException.getErrorText("Московская область", CountryUtils.COUNTRY_ID1), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное создание страна не найдена")
    void saveCountryNotFoundExceptionTest() {
        UUID countryId = UUID.randomUUID();
        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> regionService.create(new CreateRegionDTO("Брянская область", countryId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        RegionDTO actual1 = regionService.update(
                RegionUtils.REGION_ID1,
                new UpdateRegionDTO(null, CountryUtils.COUNTRY_ID2)
        );

        RegionDTO actual2 = regionService.update(
                RegionUtils.REGION_ID2,
                new UpdateRegionDTO("Брянская область", CountryUtils.COUNTRY_ID2)
        );

        RegionDTO actual3 = regionService.update(
                RegionUtils.REGION_ID3,
                new UpdateRegionDTO("Курганская область", null)
        );

        Assertions.assertEquals(RegionUtils.REGION_ID1, actual1.getId());
        Assertions.assertEquals("Московская область", actual1.getName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID2, actual1.getCountry().getId());

        Assertions.assertEquals(RegionUtils.REGION_ID2, actual2.getId());
        Assertions.assertEquals("Брянская область", actual2.getName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID2, actual2.getCountry().getId());

        Assertions.assertEquals(RegionUtils.REGION_ID3, actual3.getId());
        Assertions.assertEquals("Курганская область", actual3.getName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, actual3.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - регион не найден")
    void updateRegionNotFoundFailTest() {
        UUID regionId = UUID.randomUUID();
        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> regionService.update(regionId, new UpdateRegionDTO(null, null))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(RegionByIdNotFoundException.getErrorText(regionId), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - страна не найдена")
    void updateCountryNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();
        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> regionService.update(RegionUtils.REGION_ID1, new UpdateRegionDTO(null, countryId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - регион уже создан")
    void updateAlreadyExistsFailTest() {
        RegionAlreadyExistsException actual = Assertions.assertThrows(
                RegionAlreadyExistsException.class,
                () -> regionService.update(
                        RegionUtils.REGION_ID6,
                        new UpdateRegionDTO("Московская область", CountryUtils.COUNTRY_ID1)
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(RegionAlreadyExistsException.getErrorText("Московская область", CountryUtils.COUNTRY_ID1), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void findByIdSuccessTest() {
        RegionDetailsDTO actual = regionService.getById(RegionUtils.REGION_ID3);

        Assertions.assertEquals(RegionUtils.REGION_ID3, actual.getId());
        Assertions.assertEquals("Свердловская область", actual.getName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, actual.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - регион не найден")
    void findByIdNotFoundTest() {
        UUID regionId = UUID.randomUUID();
        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> regionService.getById(regionId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(RegionByIdNotFoundException.getErrorText(regionId), actual.getMessage());
    }

    private static Stream<Arguments> findAllTest() {
        return Stream.of(
                arguments(named("Поиск по названию региона 1", "ОБЛ"), null, 0, 100, 3),
                arguments(named("Поиск по названию региона 2", "аньХОЙ"), null, 0, 100, 1),
                arguments(named("Поиск по названию региона и страны 1", "ОвСкАя"), "РОС", 0, 10, 2),
                arguments(named("Поиск по названию региона и страны 2", "цЗяН"), "китай", 0, 10, 1),
                arguments(named("Поиск по названию региона и страны 3", "брянс"), "рос", 0, 10, 0),
                arguments(named("Поиск с пустыми названиями", ""), "", 0, 10, 6),
                arguments(named("Поиск без названий", null), null, 0, 10, 6),
                arguments(named("Пагинация 1", null), null, 5, 1, 1),
                arguments(named("Пагинация 2", null), null, 1, 3, 3)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Получение списка регионов с фильтрами")
    void findAllTest(String regionText, String countryText, int page, int perPage, int expectedSize) {
        List<RegionDTO> actual = regionService.getAll(regionText, countryText, page, perPage).getData();

        Assertions.assertEquals(expectedSize, actual.size());
    }
}
