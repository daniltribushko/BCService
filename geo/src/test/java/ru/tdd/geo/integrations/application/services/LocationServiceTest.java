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
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.locations.LocationAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.locations.LocationByIdNotFoundException;
import ru.tdd.geo.application.services.LocationService;
import ru.tdd.geo.database.repositories.LocationRepository;
import ru.tdd.geo.sql.InitLocationsSqlScrips;
import ru.tdd.geo.utils.CityUtils;
import ru.tdd.geo.utils.LocationUtils;

import java.util.UUID;

@SpringBootTest
@Testcontainers
@InitLocationsSqlScrips
@Import(value = TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест сервиса локаций")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LocationServiceTest {

    private final LocationRepository locationRepository;

    private final LocationService locationService;

    @Autowired
    public LocationServiceTest(
            LocationRepository locationRepository,
            LocationService locationService
    ) {
        this.locationRepository = locationRepository;
        this.locationService = locationService;
    }

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        LocationDTO actual = locationService.create(new CreateLocationDTO("Новая локация", CityUtils.CITY_ID2));

        long actualCount = locationRepository.count();

        Assertions.assertEquals(7, actualCount);
        Assertions.assertEquals("Новая локация", actual.getName());
        Assertions.assertEquals(CityUtils.CITY_ID2, actual.getCity().getId());
    }

    @Test
    @DisplayName("Неудачное создание - локация уже создана")
    void saveAlreadyExistsFailTest() {
        LocationAlreadyExistsException actual = Assertions.assertThrows(
                LocationAlreadyExistsException.class,
                () -> locationService.create(new CreateLocationDTO("Эрмитаж", CityUtils.CITY_ID5))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(
                LocationAlreadyExistsException.getErrorText("Эрмитаж", CityUtils.CITY_ID5),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Неудачное создание - город не найден")
    void saveCityNotFoundFailTest() {
        UUID cityId = UUID.randomUUID();
        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> locationService.create(new CreateLocationDTO("Test Location", cityId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                CityByIdNotFoundException.getErrorText(cityId),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        LocationDTO actual1 = locationService.update(
                LocationUtils.LOCATION_ID1,
                new UpdateLocationDTO("Ленинская библиотека", null)
        );

        LocationDTO actual2 = locationService.update(
                LocationUtils.LOCATION_ID2,
                new UpdateLocationDTO(null, CityUtils.CITY_ID10)
        );

        Assertions.assertEquals(LocationUtils.LOCATION_ID1, actual1.getId());
        Assertions.assertEquals(LocationUtils.LOCATION_ID2, actual2.getId());
        Assertions.assertEquals("Ленинская библиотека", actual1.getName());
        Assertions.assertEquals(CityUtils.CITY_ID10, actual2.getCity().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - локация не найдена")
    void updateLocationNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();
        LocationByIdNotFoundException actual = Assertions.assertThrows(
                LocationByIdNotFoundException.class,
                () -> locationService.update(locationId, new UpdateLocationDTO())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                LocationByIdNotFoundException.getErrorText(locationId),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Неудачное обновление - локация уже создана")
    void updateAlreadyExistsFailTest() {
        LocationAlreadyExistsException actual1 = Assertions.assertThrows(
                LocationAlreadyExistsException.class,
                () -> locationService.update(
                       LocationUtils.LOCATION_ID2,
                        new UpdateLocationDTO("Российская государственная библиотека", null)
                )
        );

        LocationAlreadyExistsException actual2 = Assertions.assertThrows(
                LocationAlreadyExistsException.class,
                () -> locationService.update(
                        LocationUtils.LOCATION_ID1,
                        new UpdateLocationDTO("Эрмитаж", CityUtils.CITY_ID5)
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual1.getStatusCode());
        Assertions.assertEquals(HttpStatus.CONFLICT, actual2.getStatusCode());
        Assertions.assertEquals(
                LocationAlreadyExistsException.getErrorText("Российская государственная библиотека", CityUtils.CITY_ID4),
                actual1.getMessage()
        );

        Assertions.assertEquals(
                LocationAlreadyExistsException.getErrorText("Эрмитаж", CityUtils.CITY_ID5),
                actual2.getMessage()
        );
    }

    @Test
    @DisplayName("Неудачное обновление - город не найден")
    void updateCityNotFoundFailTest() {
        UUID cityId = UUID.randomUUID();
        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> locationService.update(LocationUtils.LOCATION_ID6, new UpdateLocationDTO(null, cityId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                CityByIdNotFoundException.getErrorText(cityId),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        LocationDTO actual1 = locationService.getById(LocationUtils.LOCATION_ID3);
        LocationDTO actual2 = locationService.getById(LocationUtils.LOCATION_ID6);

        Assertions.assertEquals(LocationUtils.LOCATION_ID3, actual1.getId());
        Assertions.assertEquals(LocationUtils.LOCATION_ID6, actual2.getId());
        Assertions.assertEquals(
                "Эрмитаж",
                actual1.getName()
        );
        Assertions.assertEquals(
                "Площадь Святого Петра",
                actual2.getName()
        );
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - локация не найдена")
    void getByIdNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();
        LocationByIdNotFoundException actual = Assertions.assertThrows(
                LocationByIdNotFoundException.class,
                () -> locationService.getById(locationId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                LocationByIdNotFoundException.getErrorText(locationId),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        locationService.delete(LocationUtils.LOCATION_ID5);

        long actualCount = locationRepository.count();

        Assertions.assertEquals(5, actualCount);
    }

    @Test
    @DisplayName("Неудачное удаление - локация не найдена")
    void deleteNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();
        LocationByIdNotFoundException actual = Assertions.assertThrows(
                LocationByIdNotFoundException.class,
                () -> locationService.delete(locationId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(
                LocationByIdNotFoundException.getErrorText(locationId),
                actual.getMessage()
        );
    }
}
