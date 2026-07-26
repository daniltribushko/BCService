package ru.tdd.geo.unit.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import ru.tdd.geo.application.mappers.LocationMapper;
import ru.tdd.geo.application.models.dto.geo.city.CityDTO;
import ru.tdd.geo.application.models.dto.geo.location.CreateLocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.LocationDTO;
import ru.tdd.geo.application.models.dto.geo.location.UpdateLocationDTO;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.locations.LocationAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.locations.LocationByIdNotFoundException;
import ru.tdd.geo.application.services.imp.LocationServiceImp;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Location;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.LocationRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Tribushko Danil
 * @since 24.01.2026
 * Набор unit-тестов для сервиса по работе с локациями
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тест сервиса локаций")
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private LocationServiceImp locationService;

    @Test
    @DisplayName("Удачное создание")
    void createSuccessTest() {
        UUID cityId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();

        Country country = new Country("Россия");
        country.setId(countryId);
        City city = new City("Москва", null, country);
        city.setId(cityId);

        Mockito.when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        Mockito.when(locationRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(locationMapper.toDto(any(Location.class)))
                .thenReturn(
                        new LocationDTO(
                                locationId,
                                "Мавзолей",
                                new CityDTO(
                                        cityId,
                                        null,
                                        null,
                                        null
                                )
                        )
                );

        LocationDTO actual = locationService.create(new CreateLocationDTO("Мавзолей", cityId));

        Mockito.verify(locationRepository).save(any(Location.class));

        Assertions.assertEquals(locationId, actual.getId());
        Assertions.assertEquals("Мавзолей", actual.getName());
        Assertions.assertEquals(cityId, actual.getCity().getId());
    }

    @Test
    @DisplayName("Неудачное создание - локация уже создана")
    void createAlreadyExistsFailTest() {
        UUID cityId = UUID.randomUUID();

        Mockito.when(locationRepository.exists(any(Specification.class))).thenReturn(true);

        LocationAlreadyExistsException actual = Assertions.assertThrows(
                LocationAlreadyExistsException.class,
                () -> locationService.create(new CreateLocationDTO("Мавзолей", cityId))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(
                LocationAlreadyExistsException.getErrorText("Мавзолей", cityId),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Неудачное создание - город не найден")
    void createCityNotFoundFailTest() {
        UUID cityId = UUID.randomUUID();

        Mockito.when(locationRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> locationService.create(new CreateLocationDTO("", cityId))
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
        UUID locationId = UUID.randomUUID();
        UUID cityId = UUID.randomUUID();

        Country country = new Country("Россия");

        City city = new City("Москва", null, country);
        city.setId(cityId);

        Location location = new Location(
                "Мавзолей",
                city
        );

        location.setId(locationId);


        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(locationRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(locationMapper.toDto(any(Location.class)))
                .thenReturn(
                        new LocationDTO(
                                locationId,
                                "Красная площадь",
                                new CityDTO(
                                        city.getId(),
                                        null,
                                        null,
                                        null
                                )
                        )
                );

        LocationDTO actual = locationService.update(
                locationId,
                new UpdateLocationDTO(
                        "Красная площадь",
                        null
                )
        );

        Assertions.assertEquals("Красная площадь", actual.getName());
        Assertions.assertEquals(city.getId(), actual.getCity().getId());
    }

    @Test
    @DisplayName("Неудачное обновление - локация не найдена")
    void updateLocationNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        LocationByIdNotFoundException actual = Assertions.assertThrows(
                LocationByIdNotFoundException.class,
                () -> locationService.update(locationId, new UpdateLocationDTO())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(LocationByIdNotFoundException.getErrorText(locationId), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление - локация уже создана")
    void updateAlreadyExistsFailTest() {
        UUID locationId = UUID.randomUUID();
        UUID cityId = UUID.randomUUID();

        City city = new City("Test City", null, null);
        city.setId(cityId);

        Location location = new Location(
                "Мавзолей",
                city
        );

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(locationRepository.exists(any(Specification.class))).thenReturn(true);

        LocationAlreadyExistsException actual = Assertions.assertThrows(
                LocationAlreadyExistsException.class,
                () -> locationService.update(locationId, new UpdateLocationDTO("Красная площадь", null))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(
                LocationAlreadyExistsException.getErrorText("Красная площадь", cityId),
                actual.getMessage()
        );
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        UUID locationId = UUID.randomUUID();
        Location location = new Location(
                "Мавзолей",
                null
        );

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));

        locationService.delete(locationId);

        Mockito.verify(locationRepository).delete(location);
    }

    @Test
    @DisplayName("Неудачное удаление локация не найдена")
    void deleteLocationNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

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

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() {
        UUID countryId = UUID.randomUUID();
        UUID cityId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();

        Country country = new Country("Россия");
        country.setId(countryId);

        City city = new City("Москва", null, country);
        city.setId(cityId);

        Location location = new Location(
                "Мавзолей",
               city
        );
        location.setId(locationId);

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(locationMapper.toDto(location))
                .thenReturn(
                        new LocationDTO(
                                locationId,
                                "Мавзолей",
                                null
                        )
                );

        LocationDTO actual = locationService.getById(locationId);

        Assertions.assertEquals(locationId, actual.getId());
        Assertions.assertEquals("Мавзолей", actual.getName());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - локация не найдена")
    void getByIdLocationNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

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
}
