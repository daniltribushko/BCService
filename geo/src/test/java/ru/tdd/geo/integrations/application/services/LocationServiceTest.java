package ru.tdd.geo.integrations.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
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
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Location;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.LocationRepository;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LocationServiceTest {

    private final CountryRepository countryRepository;

    private final CityRepository cityRepository;

    private final LocationRepository locationRepository;

    private final LocationService locationService;

    @Autowired
    public LocationServiceTest(
            CountryRepository countryRepository,
            CityRepository cityRepository,
            LocationRepository locationRepository,
            LocationService locationService
    ) {
        this.countryRepository = countryRepository;
        this.cityRepository = cityRepository;
        this.locationRepository = locationRepository;
        this.locationService = locationService;
    }

    @BeforeEach
    void cleanDb() {
        locationRepository.deleteAll();
        cityRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void saveSuccessTest() {
        Country country = new Country("Save Location Test Country");

        countryRepository.save(country);

        City city = new City("Save Location Test City", null, country);

        cityRepository.save(city);

        long expectedCount = locationRepository.count() + 1;

        LocationDTO actual = locationService.create(new CreateLocationDTO("Save Test Location", city.getId()));

        long actualCount = locationRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
        Assertions.assertEquals("Save Test Location", actual.getName());
        Assertions.assertEquals(city.getId(), actual.getCity().getId());
    }

    @Test
    void saveAlreadyExistsFailTest() {
        Country country = new Country("Already Exists Location Test Country");

        countryRepository.save(country);

        City city = new City("Already Exists Location Test City", null, country);

        cityRepository.save(city);

        Location location = new Location("Already Exists Location", city);

        locationRepository.save(location);

        LocationAlreadyExistsException actual = Assertions.assertThrows(
                LocationAlreadyExistsException.class,
                () -> locationService.create(new CreateLocationDTO("Already Exists Location", city.getId()))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Локация с указанным названием и городом уже создана",
                actual.getMessage()
        );
    }

    @Test
    void saveCityNotFoundFailTest() {
        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> locationService.create(new CreateLocationDTO("Test Location", UUID.randomUUID()))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Город с указанным идентификатором не найден",
                actual.getMessage()
        );
    }

    @Test
    void updateSuccessTest() {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        City city1 = new City("Test City 1", null, country);
        City city2 = new City("Test City 2", null, country);

        cityRepository.saveAll(List.of(city1, city2));

        Location location = new Location("Test Location", city1);

        locationRepository.save(location);

        UUID locationId = location.getId();

        LocationDTO actual1 = locationService.update(
                locationId,
                new UpdateLocationDTO("New Location", null)
        );

        LocationDTO actual2 = locationService.update(
                locationId,
                new UpdateLocationDTO(null, city2.getId())
        );

        Assertions.assertEquals(locationId, actual1.getId());
        Assertions.assertEquals(locationId, actual2.getId());
        Assertions.assertEquals("New Location", actual1.getName());
        Assertions.assertEquals(city2.getId(), actual2.getCity().getId());
    }

    @Test
    void updateLocationNotFoundFailTest() {
        LocationByIdNotFoundException actual = Assertions.assertThrows(
                LocationByIdNotFoundException.class,
                () -> locationService.update(UUID.randomUUID(), new UpdateLocationDTO())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Локация с указанным идентификатором не найдена",
                actual.getMessage()
        );
    }

    @Test
    void updateAlreadyExistsFailTest() {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        City city1 = new City("Test City", null, country);
        City city2 = new City("Test City", null, country);

        cityRepository.saveAll(List.of(city1, city2));

        Location location1 = new Location("Test Location", city1);
        Location location2 = new Location("Already Exists Location", city1);
        Location location3 = new Location("Location", city2);

        locationRepository.saveAll(List.of(location1, location2, location3));

        LocationAlreadyExistsException actual1 = Assertions.assertThrows(
                LocationAlreadyExistsException.class,
                () -> locationService.update(
                        location1.getId(),
                        new UpdateLocationDTO("Already Exists Location", null)
                )
        );

        LocationAlreadyExistsException actual2 = Assertions.assertThrows(
                LocationAlreadyExistsException.class,
                () -> locationService.update(
                        location1.getId(),
                        new UpdateLocationDTO("Location", city2.getId())
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual1.getStatusCode());
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual2.getStatusCode());
        Assertions.assertEquals(
                "Локация с указанным названием и городом уже создана",
                actual1.getMessage()
        );

        Assertions.assertEquals(
                "Локация с указанным названием и городом уже создана",
                actual2.getMessage()
        );
    }

    @Test
    void updateCityNotFoundFailTest() {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        Location location = new Location("Test Location", city);

        locationRepository.save(location);

        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> locationService.update(location.getId(), new UpdateLocationDTO(null, UUID.randomUUID()))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Город с указанным идентификатором не найден",
                actual.getMessage()
        );
    }

    @Test
    void getByIdSuccessTest() {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        Location location1 = new Location("Location 1", city);
        Location location2 = new Location("Location 2", city);
        Location location3 = new Location("Location 3", city);

        locationRepository.saveAll(
                List.of(location1, location2, location3)
        );

        LocationDTO actual1 = locationService.getById(location1.getId());
        LocationDTO actual2 = locationService.getById(location3.getId());

        Assertions.assertEquals(location1.getId(), actual1.getId());
        Assertions.assertEquals(location3.getId(), actual2.getId());
        Assertions.assertEquals(
                "Location 1",
                actual1.getName()
        );
        Assertions.assertEquals(
                "Location 3",
                actual2.getName()
        );
    }

    @Test
    void getByIdNotFoundFailTest() {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        Location location = new Location("Location", city);

        locationRepository.save(location);

        LocationByIdNotFoundException actual = Assertions.assertThrows(
                LocationByIdNotFoundException.class,
                () -> locationService.getById(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Локация с указанным идентификатором не найдена",
                actual.getMessage()
        );
    }

    @Test
    void deleteSuccessTest() {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        Location location1 = new Location("Location 1", city);
        Location location2 = new Location("Location 2", city);

        locationRepository.saveAll(List.of(location1, location2));

        long expectedCount = locationRepository.count() - 1;

        locationService.delete(location2.getId());

        long actualCount = locationRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void deleteNotFoundFailTest() {
        LocationByIdNotFoundException actual = Assertions.assertThrows(
                LocationByIdNotFoundException.class,
                () -> locationService.delete(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals(
                "Локация с указанным идентификатором не найдена",
                actual.getMessage()
        );
    }
}
