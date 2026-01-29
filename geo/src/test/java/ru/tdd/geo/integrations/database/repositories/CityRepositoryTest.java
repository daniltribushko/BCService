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
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.C;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;
import ru.tdd.geo.database.specifications.CitySpecification;
import ru.tdd.geo.database.specifications.NameSpecification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 02.01.2026
 * Набор тестов репозитория городов
 */
@DataJpaTest
@Transactional
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RegionRepository regionRepository;

    @BeforeEach
    void cleanDb() {
        cityRepository.deleteAll();
        regionRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void saveTest() {
        Country country = new Country("Test Country Save");
        City city = new City("Test City Save", null, country);
        country.getCities().add(city);

        long expectedCount = cityRepository.count() + 1;
        countryRepository.save(country);
        long actualCount = cityRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void deleteTest() {
        Country country = new Country("Test Country Delete ");
        City city = new City("Test City Delete", null, country);
        countryRepository.save(country);
        cityRepository.save(city);

        long expectedCount = cityRepository.count() - 1;
        cityRepository.delete(city);
        long actualCount = cityRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void findByIdTest() {
        Country country = new Country("Test Country Find By Id");

        City city1 = new City("Test City Find By Id 1", null, country);
        City city2 = new City("Test City Find By Id 2", null, country);

        countryRepository.save(country);
        cityRepository.save(city1);
        cityRepository.save(city2);

        Optional<City> foundCity1 = cityRepository.findById(city1.getId());
        Optional<City> foundCity2 = cityRepository.findById(city2.getId());

        Optional<City> notFoundCity1 = cityRepository.findById(UUID.randomUUID());
        Optional<City> notFoundCity2 = cityRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(foundCity1.isPresent());
        Assertions.assertTrue(foundCity2.isPresent());

        Assertions.assertEquals(city1, foundCity1.get());
        Assertions.assertEquals(city2, foundCity2.get());

        Assertions.assertFalse(notFoundCity1.isPresent());
        Assertions.assertFalse(notFoundCity2.isPresent());
    }

    @Test
    void updateTest() {
        Country country = new Country("Test Country Update");

        City city = new City("Updated City", null, country);

        countryRepository.save(country);
        cityRepository.save(city);

        city.setName("New Updated Name");
        cityRepository.save(city);

        Optional<City> updatedCity = cityRepository.findById(city.getId());

        Assertions.assertTrue(updatedCity.isPresent());
        Assertions.assertEquals("New Updated Name", updatedCity.get().getName());
    }

    @Test
    void findAllTest() {
        Country country = new Country("Test Country Find All");

        countryRepository.save(country);

        City city1 = new City("City Find All 1", null, country);
        City city2 = new City("City Find All 2", null, country);
        City city3 = new City("City Find All 3", null, country);
        City city4 = new City("City Find All 4", null, country);

        cityRepository.saveAll(List.of(city1, city2, city3, city4));

        Assertions.assertEquals(4, cityRepository.findAll().size());
    }

    @Test
    void findByNameTest() {
        Country country = new Country("Test Country Find By Name");

        countryRepository.save(country);

        City city1 = new City("TeSt CiTy", null, country);
        City city2 = new City("testING CITy", null, country);
        City city3 = new City("MosCoW", null, country);

        cityRepository.saveAll(List.of(city1, city2, city3));

        List<City> cities1 = cityRepository.findAll(NameSpecification.byNameWithFullTextSearch("test"));
        List<City> cities2 = cityRepository.findAll(NameSpecification.byNameWithFullTextSearch("iNg"));
        List<City> cities3 = cityRepository.findAll(NameSpecification.byNameWithFullTextSearch("S"));
        List<City> cities4 = cityRepository.findAll(NameSpecification.byNameWithFullTextSearch("AAAA"));

        Assertions.assertEquals(2, cities1.size());
        Assertions.assertEquals(1, cities2.size());
        Assertions.assertEquals(3, cities3.size());
        Assertions.assertEquals(0, cities4.size());
    }

    @Test
    void existsByNameAndRegionAndCityTest() {
        Country country1 = new Country("Test Country 1");
        Country country2 = new Country("Test Country 2");

        countryRepository.saveAll(List.of(country1, country2));

        Region region = new Region("Test Region", country2);

        regionRepository.save(region);

        City city1 = new City("Oslo", null, country1);
        City city2 = new City("PEKIN", region, country2);
        City city3 = new City("MosCoW", null, country2);

        cityRepository.saveAll(List.of(city1, city2, city3));

        Assertions.assertTrue(
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                "oslo",
                                null,
                                country1.getId()
                        )
                )
        );

        Assertions.assertTrue(
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                "oSlO",
                                null,
                                country1.getId()
                        )
                )
        );

        Assertions.assertFalse(
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                "os",
                                null,
                                country1.getId()
                        )
                )
        );

        Assertions.assertTrue(
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                "pEkIn",
                                region.getId(),
                                country2.getId()
                        )
                )
        );

        Assertions.assertTrue(
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                "pekin",
                                region.getId(),
                                country2.getId()
                        )
                )
        );

        Assertions.assertFalse(
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                "pekin",
                                null,
                                country2.getId()
                        )
                )
        );

        Assertions.assertFalse(
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                "PEKinn",
                                null,
                                country2.getId()
                        )
                )
        );

        Assertions.assertTrue(
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                "moscow",
                                null,
                                country2.getId()
                        )
                )
        );

        Assertions.assertTrue(
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                "MOSCOW",
                                null,
                                country2.getId()
                        )
                )
        );

        Assertions.assertFalse(
                cityRepository.exists(
                        CitySpecification.byNameRegionCityEqual(
                                "mmoscoww",
                                null,
                                country2.getId()
                        )
                )
        );
    }
}
