package ru.tdd.geo.integrations.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.city.CityDTO;
import ru.tdd.geo.application.models.dto.geo.city.CityDetailsDTO;
import ru.tdd.geo.application.models.dto.geo.city.CreateCityDTO;
import ru.tdd.geo.application.models.dto.geo.city.UpdateCityDTO;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityAlreadyExistException;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.services.CityService;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;
import ru.tdd.geo.sql.InitCitiesSqlScrips;
import ru.tdd.geo.utils.CityUtils;
import ru.tdd.geo.utils.CountryUtils;
import ru.tdd.geo.utils.RegionUtils;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 18.01.2026
 * Набор тестор сервиса для работы с городами
 */
@SpringBootTest
@Testcontainers
@InitCitiesSqlScrips
@Import(value = TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест сервиса по работе с городами")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CityServiceTest {

    private final CityRepository cityRepository;


    private final CityService cityService;

    @Autowired
    CityServiceTest(
            CityRepository cityRepository,
            CityService cityService
    ) {
        this.cityRepository = cityRepository;
        this.cityService = cityService;
    }

    @Test
    @DisplayName("Удачное создание")
    void createSuccessTest() {
        long expected = cityRepository.count() + 2;

        CityDTO actual1 = cityService.create(new CreateCityDTO("Сургут", null, CountryUtils.COUNTRY_ID1));
        CityDTO actual2 = cityService.create(new CreateCityDTO("Нижний Новгород", RegionUtils.REGION_ID5, null));

        long actual = cityRepository.count();

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals("Сургут", actual1.getName());
        Assertions.assertEquals("Нижний Новгород", actual2.getName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, actual1.getCountry().getId());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID2, actual2.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное создание - страна уже создана")
    void createAlreadyExistsFailTest() {
        CityAlreadyExistException actual = Assertions.assertThrows(
                CityAlreadyExistException.class,
                () -> cityService.create(new CreateCityDTO("Москва", null, CountryUtils.COUNTRY_ID1))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(CityAlreadyExistException.getErrorText("Москва", CountryUtils.COUNTRY_ID1, null), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное создание - регион не найден")
    void createRegionNotFoundFailTest() {
        UUID regionId = UUID.randomUUID();
        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> cityService.create(new CreateCityDTO("New City", regionId, null))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(RegionByIdNotFoundException.getErrorText(regionId), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное создание - страна не найдена")
    void createCountryNouFoundFailTest() {
        UUID countryId = UUID.randomUUID();
        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> cityService.create(new CreateCityDTO("New City", null, countryId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {

        CityDTO actual1 = cityService.update(CityUtils.CITY_ID1, new UpdateCityDTO("Новая москва", null, null));
        CityDTO actual2 = cityService.update(CityUtils.CITY_ID1, new UpdateCityDTO(null, RegionUtils.REGION_ID2, null));
        CityDTO actual3 = cityService.update(CityUtils.CITY_ID1, new UpdateCityDTO(null, RegionUtils.REGION_ID4, null));
        CityDTO actual4 = cityService.update(CityUtils.CITY_ID1, new UpdateCityDTO(null, null, CountryUtils.COUNTRY_ID3));

        Assertions.assertEquals("Новая москва", actual1.getName());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, actual1.getCountry().getId());

        Assertions.assertEquals(RegionUtils.REGION_ID2, actual2.getRegion().getId());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID1, actual2.getCountry().getId());

        Assertions.assertEquals(RegionUtils.REGION_ID4, actual3.getRegion().getId());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID2, actual3.getCountry().getId());

        Assertions.assertNull(actual4.getRegion());
        Assertions.assertEquals(CountryUtils.COUNTRY_ID3, actual4.getCountry().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - город не найден")
    void updateCityNotFoundFailTest() {
        UUID cityId = UUID.randomUUID();
        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> cityService.update(cityId, new UpdateCityDTO())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CityByIdNotFoundException.getErrorText(cityId), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - регион не найден")
    void updateRegionNotFoundFailTest() {
        UUID regionId = UUID.randomUUID();
        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> cityService.update(CityUtils.CITY_ID6, new UpdateCityDTO(null, regionId, null))
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
                () -> cityService.update(CityUtils.CITY_ID2, new UpdateCityDTO(null, null, countryId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - город уже создан")
    void updateAlreadyExistsFailTest() {
        CityAlreadyExistException actual1 = Assertions.assertThrows(
                CityAlreadyExistException.class,
                () -> cityService.update(
                        CityUtils.CITY_ID1,
                        new UpdateCityDTO(
                                null,
                                RegionUtils.REGION_ID1,
                                null
                        )
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual1.getStatusCode());
        Assertions.assertEquals(CityAlreadyExistException.getErrorText("Дзержинск", CountryUtils.COUNTRY_ID1, RegionUtils.REGION_ID1), actual1.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        CityDetailsDTO actual1 = cityService.getById(CityUtils.CITY_ID4);
        CityDetailsDTO actual2 = cityService.getById(CityUtils.CITY_ID10);

        Assertions.assertEquals(CityUtils.CITY_ID4, actual1.getId());
        Assertions.assertEquals(CityUtils.CITY_ID10, actual2.getId());
        Assertions.assertEquals("Москва", actual1.getName());
        Assertions.assertEquals("Харбин", actual2.getName());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - город не найден")
    void getByIdNotFoundTest() {
        UUID cityId = UUID.randomUUID();
        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> cityService.getById(cityId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CityByIdNotFoundException.getErrorText(cityId), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        cityService.delete(CityUtils.CITY_ID2);
        long actual = cityRepository.count();

        Assertions.assertEquals(10, actual);
    }
}
