package ru.tdd.geo.unit.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private LocationServiceImp locationService;

    @Test
    void createSuccessTest() {
        UUID cityId = UUID.randomUUID();

        City city = new City("Test City", null, new Country("Test Country"));
        city.setId(cityId);

        Mockito.when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        Mockito.when(locationRepository.exists(any(Specification.class))).thenReturn(false);

        LocationDTO actual = locationService.create(new CreateLocationDTO("Test Location", cityId));

        Mockito.verify(locationRepository).save(any(Location.class));

        Assertions.assertEquals("Test Location", actual.getName());
        Assertions.assertEquals(cityId, actual.getCity().getId());
    }

    @Test
    void createAlreadyExistsFailTest() {
        Mockito.when(locationRepository.exists(any(Specification.class))).thenReturn(true);

        LocationAlreadyExistsException actual = Assertions.assertThrows(
                LocationAlreadyExistsException.class,
                () -> locationService.create(new CreateLocationDTO("Test City", UUID.randomUUID()))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Локация с указанным названием и городом уже создана",
                actual.getMessage()
        );
    }

    @Test
    void createCityNotFoundFailTest() {
        UUID cityId = UUID.randomUUID();

        Mockito.when(locationRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> locationService.create(new CreateLocationDTO("", cityId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Город с указанным идентификатором не найден",
                actual.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {
        UUID locationId = UUID.randomUUID();
        UUID newCityId = UUID.randomUUID();

        Country country = new Country("Test Country");

        Location location = new Location(
                "Test Location",
                new City(
                        "Test City",
                        null,
                        country
                )
        );
        location.setId(locationId);

        City city = new City("New City", null, country);
        city.setId(newCityId);

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(cityRepository.findById(newCityId)).thenReturn(Optional.of(city));
        Mockito.when(locationRepository.exists(any(Specification.class))).thenReturn(false);

        LocationDTO actual = locationService.update(
                locationId,
                new UpdateLocationDTO(
                        "Updated Location",
                        city.getId()
                )
        );

        Assertions.assertEquals("Updated Location", actual.getName());
        Assertions.assertEquals(city.getId(), actual.getCity().getId());
    }

    @Test
    void updateLocationNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        LocationByIdNotFoundException actual = Assertions.assertThrows(
                LocationByIdNotFoundException.class,
                () -> locationService.update(locationId, new UpdateLocationDTO())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Локация с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void updateAlreadyExistsFailTest() {
        UUID locationId = UUID.randomUUID();
        Location location = new Location(
                "Test Location",
                new City("Test City", null, null)
        );

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        Mockito.when(locationRepository.exists(any(Specification.class))).thenReturn(true);

        LocationAlreadyExistsException actual = Assertions.assertThrows(
                LocationAlreadyExistsException.class,
                () -> locationService.update(locationId, new UpdateLocationDTO("New Location", null))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Локация с указанным названием и городом уже создана",
                actual.getMessage()
        );
    }

    @Test
    void deleteSuccessTest() {
        UUID locationId = UUID.randomUUID();
        Location location = new Location(
                "Test Location",
                null
        );

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));

        locationService.delete(locationId);

        Mockito.verify(locationRepository).delete(location);
    }

    @Test
    void deleteLocationNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        LocationByIdNotFoundException actual = Assertions.assertThrows(
                LocationByIdNotFoundException.class,
                () -> locationService.delete(locationId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Локация с указанным идентификатором не найдена",
                actual.getMessage()
        );
    }

    @Test
    void getByIdSuccessTest() {
        UUID locationId = UUID.randomUUID();

        Location location = new Location(
                "Test Location",
                new City(
                        "Test City",
                        null,
                        new Country("Test Country")
                )
        );
        location.setId(locationId);

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));

        LocationDTO actual = locationService.getById(locationId);

        Assertions.assertEquals(locationId, actual.getId());
        Assertions.assertEquals("Test Location", actual.getName());
    }

    @Test
    void getByIdLocationNotFoundFailTest() {
        UUID locationId = UUID.randomUUID();

        Mockito.when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

        LocationByIdNotFoundException actual = Assertions.assertThrows(
                LocationByIdNotFoundException.class,
                () -> locationService.getById(locationId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Локация с указанным идентификатором не найдена",
                actual.getMessage()
        );
    }
}
