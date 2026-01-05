package ru.tdd.geo.integrations.database.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Location;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.LocationRepository;
import ru.tdd.geo.database.specifications.NameSpecification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.01.2026
 * Набор тестов репозитория локаций
 */
@DataJpaTest
@Transactional
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    void cleanDb() {
        locationRepository.deleteAll();
        cityRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void saveTest() {
        Country country = new Country("Save Country Test");
        City city = new City("Save City Test", null, country);
        Location location = new Location("Save Location Test", city);

        countryRepository.save(country);
        cityRepository.save(city);

        long expectedCount = locationRepository.count() + 1;
        locationRepository.save(location);
        long actualCount = locationRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void deleteTest() {
        Country country = new Country("Delete Country Test");
        City city = new City("Delete City Test", null, country);
        Location location = new Location("Delete Location Test", city);

        countryRepository.save(country);
        cityRepository.save(city);
        locationRepository.save(location);

        long expectedCount = locationRepository.count() - 1;
        locationRepository.delete(location);
        long actualCount = locationRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void findByIdTest() {
        Country country = new Country("Find By Id Country Test");
        City city = new City("Find By Id City Test", null, country);
        Location location1 = new Location("Find By Id Location Test 1", city);
        Location location2 = new Location("Find By Id Location Test 2", city);

        countryRepository.save(country);
        cityRepository.save(city);

        locationRepository.save(location1);
        locationRepository.save(location2);

        Optional<Location> foundLocation1 = locationRepository.findById(location1.getId());
        Optional<Location> foundLocation2 = locationRepository.findById(location2.getId());

        Optional<Location> notFoundLocation1 = locationRepository.findById(UUID.randomUUID());
        Optional<Location> notFoundLocation2 = locationRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(foundLocation1.isPresent());
        Assertions.assertTrue(foundLocation2.isPresent());

        Assertions.assertEquals(location1, foundLocation1.get());
        Assertions.assertEquals(location2, foundLocation2.get());

        Assertions.assertFalse(notFoundLocation1.isPresent());
        Assertions.assertFalse(notFoundLocation2.isPresent());
    }

    @Test
    void updateTest() {
        Country country = new Country("Update Country Test");
        City city = new City("Update City Test", null, country);
        Location location = new Location("Update Location Test 1", city);

        countryRepository.save(country);
        cityRepository.save(city);
        locationRepository.save(location);

        location.setName("Updated New Location Name");

        locationRepository.save(location);

        Optional<Location> updatedLocation = locationRepository.findById(location.getId());

        Assertions.assertTrue(updatedLocation.isPresent());
        Assertions.assertEquals("Updated New Location Name", updatedLocation.get().getName());
    }

    @Test
    void findAllTest() {
        Country country = new Country("Find All Country Test");
        City city = new City("Find All City Test", null, country);
        Location location1 = new Location("Find All Location Test 1", city);
        Location location2 = new Location("Find All Location Test 2", city);
        Location location3 = new Location("Find All Location Test 3", city);

        countryRepository.save(country);
        cityRepository.save(city);
        locationRepository.saveAll(List.of(location1, location2, location3));

        Assertions.assertEquals(3, locationRepository.findAll().size());
    }

    @Test
    void findByNameTest() {
        Country country = new Country("Find By Name Country Test");
        City city = new City("Find By Name City Test", null, country);

        Location location1 = new Location("Test location", city);
        Location location2 = new Location("loc", city);
        Location location3 = new Location("testing", city);

        countryRepository.save(country);
        cityRepository.save(city);
        locationRepository.saveAll(List.of(location1, location2, location3));

        List<Location> locations1 = locationRepository.findAll(NameSpecification.byNameWithFullTextSearch("Loc"));
        List<Location> locations2 = locationRepository.findAll(NameSpecification.byNameWithFullTextSearch("tESt"));
        List<Location> locations3 = locationRepository.findAll(NameSpecification.byNameWithFullTextSearch("TeSTinG"));
        List<Location> locations4 = locationRepository.findAll(NameSpecification.byNameWithFullTextSearch("Qwerty"));

        Assertions.assertEquals(2, locations1.size());
        Assertions.assertEquals(2, locations2.size());
        Assertions.assertEquals(1, locations3.size());
        Assertions.assertEquals(0, locations4.size());
    }

    @Test
    void existsByNameTest() {
        Country country = new Country("Exists By Name Country Test");
        City city = new City("EXists By Name City Test", null, country);

        Location location1 = new Location("Test Exists By Name", city);
        Location location2 = new Location("lOcAtion", city);

        countryRepository.save(country);
        cityRepository.save(city);
        locationRepository.saveAll(List.of(location1, location2));

        Assertions.assertTrue(locationRepository.exists(NameSpecification.byNameEqual("test exists by name")));
        Assertions.assertTrue(locationRepository.exists(NameSpecification.byNameEqual("LOCATION")));
        Assertions.assertFalse(locationRepository.exists(NameSpecification.byNameEqual("New location")));
    }
}
