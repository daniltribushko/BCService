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
import ru.tdd.geo.application.models.dto.geo.city.CityDTO;
import ru.tdd.geo.application.models.dto.geo.city.CityDetailsDTO;
import ru.tdd.geo.application.models.dto.geo.city.CreateCityDTO;
import ru.tdd.geo.application.models.dto.geo.city.UpdateCityDTO;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityAlreadyExistException;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.country.CountryByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.services.CityService;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 18.01.2026
 * Набор тестор сервиса для работы с городами
 */
@SpringBootTest
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CityServiceTest {

    private final CityRepository cityRepository;

    private final CountryRepository countryRepository;

    private final RegionRepository regionRepository;

    private final CityService cityService;

    @Autowired
    CityServiceTest(
            CityRepository cityRepository,
            CountryRepository countryRepository,
            RegionRepository regionRepository,
            CityService cityService
    ) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
        this.cityService = cityService;
    }

    @BeforeEach
    void cleanDB() {
        cityRepository.deleteAll();
        regionRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void createSuccessTest() {
        Country country = new Country("Save Test Country");

        countryRepository.save(country);

        Region region =  new Region("Save Test Region", country);

        regionRepository.save(region);

        long expected = cityRepository.count() + 2;

        CityDTO actual1 = cityService.create(new CreateCityDTO("New City 1", null, country.getId()));
        CityDTO actual2 = cityService.create(new CreateCityDTO("New City 2", region.getId(), null));

        long actual = cityRepository.count();

        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals("New City 1", actual1.getName());
        Assertions.assertEquals("New City 2", actual2.getName());
        Assertions.assertEquals(country.getId(), actual1.getCountry().getId());
        Assertions.assertEquals(country.getId(), actual2.getCountry().getId());
    }

    @Test
    void createAlreadyExistsFailTest() {
        Country country = new Country("Already Exists Country");

        countryRepository.save(country);

        City city = new City("Already Exists City", null, country);

        cityRepository.save(city);

        CityAlreadyExistException actual = Assertions.assertThrows(
                CityAlreadyExistException.class,
                () -> cityService.create(new CreateCityDTO("Already Exists City", null, country.getId()))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Город с указанным названием, страной, регионом уже создан", actual.getMessage());
    }

    @Test
    void createRegionNotFoundFailTest() {
        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> cityService.create(new CreateCityDTO("New City", UUID.randomUUID(), null))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void createCountryNouFoundFailTest() {
        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> cityService.create(new CreateCityDTO("New City", null, UUID.randomUUID()))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void updateSuccessTest() {
        Country country1 = new Country("Country For Update 1");
        Country country2 = new Country("Country For Update 2");

        countryRepository.saveAll(List.of(country1, country2));

        Region region1 = new Region("Region 1", country1);
        Region region2 = new Region("Region 2", country2);

        regionRepository.saveAll(List.of(region1, region2));

        City city = new City("City", null, country1);

        cityRepository.save(city);

        CityDTO actual1 = cityService.update(city.getId(), new UpdateCityDTO("New City Test", null, null));
        CityDTO actual2 = cityService.update(city.getId(), new UpdateCityDTO(null, region1.getId(), null));
        CityDTO actual3 = cityService.update(city.getId(), new UpdateCityDTO(null, region2.getId(), null));
        CityDTO actual4 = cityService.update(city.getId(), new UpdateCityDTO(null, null, country1.getId()));

        Assertions.assertEquals("New City Test", actual1.getName());
        Assertions.assertEquals(country1.getId(), actual1.getCountry().getId());

        Assertions.assertEquals(region1.getId(), actual2.getRegion().getId());
        Assertions.assertEquals(country1.getId(), actual2.getCountry().getId());

        Assertions.assertEquals(region2.getId(), actual3.getRegion().getId());
        Assertions.assertEquals(country2.getId(), actual3.getCountry().getId());

        Assertions.assertNull(actual4.getRegion());
        Assertions.assertEquals(country1.getId(), actual4.getCountry().getId());
    }

    @Test
    void updateCityNotFoundFailTest() {
        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> cityService.update(UUID.randomUUID(), new UpdateCityDTO())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Город с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void updateRegionNotFoundFailTest() {
        Country country = new Country("Test Country");

        countryRepository.save(country);

        City city = new City("Test City", null, country);

        cityRepository.save(city);

        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> cityService.update(city.getId(), new UpdateCityDTO(null, UUID.randomUUID(), null))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void updateCountryNotFoundFailTest() {
        Country country = new Country("Test Country 2");

        countryRepository.save(country);

        City city = new City("Test City 2", null, country);

        cityRepository.save(city);

        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> cityService.update(city.getId(), new UpdateCityDTO(null, null, UUID.randomUUID()))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void updateAlreadyExistsFailTest() {
        Country country1 = new Country("Test Update Country 1");
        Country country2 = new Country("Test Update Country 2");

        countryRepository.saveAll(List.of(country1, country2));

        Region region = new Region("Region", country1);

        regionRepository.save(region);

        City city1 = new City("City For Update", null, country1);
        City city2 = new City("City For Update 2", region, country1);

        cityRepository.saveAll(List.of(city1, city2));

        CityAlreadyExistException actual1 = Assertions.assertThrows(
                CityAlreadyExistException.class,
                () -> cityService.update(
                        city1.getId(),
                        new UpdateCityDTO(
                                "City For Update 2",
                                region.getId(),
                                null
                        )
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual1.getStatusCode());
        Assertions.assertEquals("Город с указанным названием, страной, регионом уже создан", actual1.getMessage());
    }

    @Test
    void getByIdSuccessTest() {
        Country country = new Country("Get By Id Test Country");

        countryRepository.save(country);

        City city1 = new City("Get By Id City 1", null, country);
        City city2 = new City("Get By Id City 2", null, country);

        cityRepository.saveAll(List.of(city1, city2));

        CityDetailsDTO actual1 = cityService.getById(city1.getId());
        CityDetailsDTO actual2 = cityService.getById(city2.getId());

        Assertions.assertEquals(city1.getId(), actual1.getId());
        Assertions.assertEquals(city2.getId(), actual2.getId());
        Assertions.assertEquals("Get By Id City 1", actual1.getName());
        Assertions.assertEquals("Get By Id City 2", actual2.getName());
    }

    @Test
    void getByIdNotFoundTest() {
        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> cityService.getById(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Город с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void deleteSuccessTest() {
        Country country = new Country("City's Country For Delete");

        countryRepository.save(country);

        City city = new City("City For Delete", null, country);

        cityRepository.save(city);

        long expected = cityRepository.count() - 1;
        cityService.delete(city.getId());
        long actual = cityRepository.count();

        Assertions.assertEquals(expected, actual);
    }
}
