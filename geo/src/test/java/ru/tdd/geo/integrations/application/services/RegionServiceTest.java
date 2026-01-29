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
import ru.tdd.geo.application.models.dto.geo.region.*;
import ru.tdd.geo.application.models.exceptions.geo.country.CountryByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.services.RegionService;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 09.01.2026
 * Набор интеграционных тестов сервиса по работе с регионами
 */
@SpringBootTest
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RegionServiceTest {

    private final RegionRepository regionRepository;

    private final CountryRepository countryRepository;

    private final RegionService regionService;

    @Autowired
    RegionServiceTest(RegionRepository regionRepository, CountryRepository countryRepository, RegionService regionService) {
        this.regionRepository = regionRepository;
        this.countryRepository = countryRepository;
        this.regionService = regionService;
    }

    @BeforeEach
    void cleanDb() {
        regionRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void saveSuccessTest() {
        Country country = new Country("Save Russia");

        countryRepository.save(country);

        regionRepository.save(new Region("Save Test Region", country));

        long expectedCount = regionRepository.count() + 1;
        RegionDTO actualDto = regionService.create(new CreateRegionDTO("Save Moscow Oblast", country.getId()));
        long actualCount = regionRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
        Assertions.assertEquals("Save Moscow Oblast", actualDto.getName());
        Assertions.assertEquals(country.getId(), actualDto.getCountry().getId());
    }

    @Test
    void saveAlreadyExistsExceptionTest() {
        Country country = new Country("Already Exists Region Test Country");
        countryRepository.save(country);
        Region region = new Region("Already Exists Region Test", country);
        regionRepository.save(region);

        RegionAlreadyExistsException actual = Assertions.assertThrows(
                RegionAlreadyExistsException.class,
                () -> regionService.create(
                        new CreateRegionDTO("Already Exists Region Test", country.getId())
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным названием и страной уже создан", actual.getMessage());
    }

    @Test
    void saveCountryNotFoundExceptionTest() {
        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> regionService.create(new CreateRegionDTO("Region Without Country", UUID.randomUUID()))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void updateSuccessTest() {
        Country country1 = new Country("Россия");
        Country country2 = new Country("Китай");
        Country country3 = new Country("Замбия");

        countryRepository.saveAll(List.of(country1, country2, country3));

        Region region1 = new Region("Московская область", country1);
        Region region2 = new Region("Пекинская область", country2);
        Region region3 = new Region("Ленинградская область", country1);

        regionRepository.saveAll(List.of(region1, region2, region3));

        RegionDTO actual1 = regionService.update(
                region1.getId(),
                new UpdateRegionDTO(null, country3.getId())
        );

        RegionDTO actual2 = regionService.update(
                region2.getId(),
                new UpdateRegionDTO("Свердловская область", country1.getId())
        );

        RegionDTO actual3 = regionService.update(
                region3.getId(),
                new UpdateRegionDTO("ХМАО", null)
        );

        Assertions.assertEquals(region1.getId(), actual1.getId());
        Assertions.assertEquals("Московская область", actual1.getName());
        Assertions.assertEquals(country3.getId(), actual1.getCountry().getId());

        Assertions.assertEquals(region2.getId(), actual2.getId());
        Assertions.assertEquals("Свердловская область", actual2.getName());
        Assertions.assertEquals(country1.getId(), actual2.getCountry().getId());

        Assertions.assertEquals(region3.getId(), actual3.getId());
        Assertions.assertEquals("ХМАО", actual3.getName());
        Assertions.assertEquals(country1.getId(), region3.getCountry().getId());
    }

    @Test
    void updateRegionNotFoundFailTest() {
        Country country = new Country("Test Update Region Country");
        countryRepository.save(country);
        Region region = new Region("Test Update Region", country);
        regionRepository.save(region);

        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> regionService.update(UUID.randomUUID(), new UpdateRegionDTO(null, null))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void updateCountryNotFoundFailTest() {
        Country country = new Country("Великобритания");
        countryRepository.save(country);
        Region region = new Region("Англия", country);
        regionRepository.save(region);

        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> regionService.update(region.getId(), new UpdateRegionDTO(null, UUID.randomUUID()))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void updateAlreadyExistsFailTest() {
        Country country1 = new Country("Казахстан");
        Country country2 = new Country("Мадагаскар");

        countryRepository.saveAll(List.of(country1, country2));

        Region region1 = new Region("Test Region", country1);
        Region region2 = new Region("Already Exists Exception", country2);

        regionRepository.saveAll(List.of(region1, region2));

        RegionAlreadyExistsException actual = Assertions.assertThrows(
                RegionAlreadyExistsException.class,
                () -> regionService.update(
                        region1.getId(),
                        new UpdateRegionDTO("Already Exists Exception", country2.getId())
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным названием и страной уже создан", actual.getMessage());
    }

    @Test
    void findByIdSuccessTest() {
        Country country = new Country("Moscow");

        countryRepository.save(country);

        Region region1 = new Region("Moscow Oblast", country);
        Region region2 = new Region("Ленинградская область", country);

        regionRepository.saveAll(List.of(region1, region2));

        RegionDetailsDTO actual = regionService.getById(region1.getId());

        Assertions.assertEquals(region1.getId(), actual.getId());
        Assertions.assertEquals("Moscow Oblast", actual.getName());
        Assertions.assertEquals(country.getId(), region1.getCountry().getId());
    }

    @Test
    void findByIdNotFoundTest() {
        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> regionService.getById(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void findAllTest() {
        Country country1 = new Country("TeSt CoUNtrY");
        Country country2 = new Country("TesTiNg");
        Country country3 = new Country("neW CoUNtRy");

        countryRepository.saveAll(List.of(country1, country2, country3));

        Region region1 = new Region("ReGioN", country1);
        Region region2 = new Region("TesT regION", country1);
        Region region3 = new Region("Московская область", country2);
        Region region4 = new Region("Свердловская область", country2);
        Region region5 = new Region("ObLASt tESt", country3);

        regionRepository.saveAll(List.of(region1, region2, region3, region4, region5));

        RegionsDTO actual1 = regionService.getAll(null, null, 0, 100);
        RegionsDTO actual2 = regionService.getAll(null, "eSt", 0, 100);
        RegionsDTO actual3 = regionService.getAll("ОбЛаСтЬ", null, 0, 100);
        RegionsDTO actual4 = regionService.getAll(null, null, 1, 4);

        Assertions.assertEquals(5, actual1.getData().size());
        Assertions.assertEquals(4, actual2.getData().size());
        Assertions.assertEquals(2, actual3.getData().size());
        Assertions.assertEquals(1, actual4.getData().size());
    }
}
