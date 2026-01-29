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
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;
import ru.tdd.geo.database.specifications.NameSpecification;
import ru.tdd.geo.database.specifications.RegionSpecification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 04.01.2026
 * Набор тестов для репозитория регионов
 */
@DataJpaTest
@Transactional
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RegionRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RegionRepository regionRepository;

    @BeforeEach
    void cleanDb() {
        regionRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void saveTest() {
        Country country = new Country("Save Test Country");
        country.addRegion(new Region("Save Test Region", country));

        long expectedCount = regionRepository.count() + 1;
        countryRepository.save(country);
        long actualCount = regionRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void deleteTest() {
        Country country = new Country("Delete Test Country");
        Region region = new Region("Delete Test Region", country);

        countryRepository.save(country);
        regionRepository.save(region);

        long expectedCount = regionRepository.count() - 1;
        regionRepository.delete(region);
        long actualCount = regionRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void findByIdTest() {
        Country country = new Country("Find By Id Test Country");
        Region region1 = new Region("Find By Id Test Region 1", country);
        Region region2 = new Region("Find By Id Test Region 2", country);

        countryRepository.save(country);
        regionRepository.saveAll(List.of(region1, region2));

        Optional<Region> foundRegion1 = regionRepository.findById(region1.getId());
        Optional<Region> foundRegion2 = regionRepository.findById(region2.getId());

        Optional<Region> notFoundRegion1 = regionRepository.findById(UUID.randomUUID());
        Optional<Region> notFoundRegion2 = regionRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(foundRegion1.isPresent());
        Assertions.assertTrue(foundRegion2.isPresent());

        Assertions.assertEquals(region1, foundRegion1.get());
        Assertions.assertEquals(region2, foundRegion2.get());

        Assertions.assertFalse(notFoundRegion1.isPresent());
        Assertions.assertFalse(notFoundRegion2.isPresent());
    }

    @Test
    void updateTest() {
        Country country = new Country("Update Test Country");
        Region region = new Region("Update Test Region", country);

        countryRepository.save(country);
        regionRepository.save(region);

        region.setName("New Updated Name");

        regionRepository.save(region);

        Optional<Region> updatedRegion = regionRepository.findById(region.getId());

        Assertions.assertTrue(updatedRegion.isPresent());
        Assertions.assertEquals("New Updated Name", region.getName());
    }

    @Test
    void findAllTest() {
        Country country = new Country("Find All Test Country");

        countryRepository.save(country);

        Region region1 = new Region("Find All Test Region 1", country);
        Region region2 = new Region("Find All Test Region 2", country);
        Region region3 = new Region("Find All Test Region 3", country);

        regionRepository.saveAll(List.of(region1, region2, region3));

        Assertions.assertEquals(3, regionRepository.findAll().size());
    }

    @Test
    void findByNameAndCountryNameTest() {
        Country country = new Country("Find By Name Test Country");
        Country country1 = new Country("CoUntRY Test");
        Country country2 = new Country("Testing");

        countryRepository.saveAll(List.of(country, country1, country2));

        Region region1 = new Region("Test DisTrIcr", country);
        Region region2 = new Region("TeSt", country1);
        Region region3 = new Region("region", country2);

        regionRepository.saveAll(List.of(region1, region2, region3));

        List<Region> regions1 = regionRepository.findAll(
                RegionSpecification.byNameAndCountryNameFullTextSearch("tE", "tEsT")
        );
        List<Region> regions2 = regionRepository.findAll(
                RegionSpecification.byNameAndCountryNameFullTextSearch("dIs", null)
        );
        List<Region> regions3 = regionRepository.findAll(
                RegionSpecification.byNameAndCountryNameFullTextSearch("GiON", null)
        );
        List<Region> regions4 = regionRepository.findAll(
                RegionSpecification.byNameAndCountryNameFullTextSearch(null, "cOuNtRy")
        );

        Assertions.assertEquals(2, regions1.size());
        Assertions.assertEquals(1, regions2.size());
        Assertions.assertEquals(1, regions3.size());
        Assertions.assertEquals(2, regions4.size());
    }

    @Test
    void existsByNameAndCountryTest() {
        Country country1 = new Country("Country 1");
        Country country2 = new Country("Country 2");

        countryRepository.saveAll(List.of(country1, country2));

        Region region1 = new Region("ХМАО", country1);
        Region region2 = new Region("Московская область", country2);
        Region region3 = new Region("Краснодарский край", country2);

        regionRepository.saveAll(List.of(region1, region2, region3));

        boolean actual1 = regionRepository.exists(
                RegionSpecification.byNameAndCountryIdEqual("ХМАО", country1.getId())
        );
        boolean actual2 = regionRepository.exists(
                RegionSpecification.byNameAndCountryIdEqual("Московская область", country1.getId())
        );
        boolean actual3 = regionRepository.exists(
                RegionSpecification.byNameAndCountryIdEqual("Краснодарский край", country2.getId())
        );

        Assertions.assertTrue(actual1);
        Assertions.assertFalse(actual2);
        Assertions.assertTrue(actual3);
    }
}
