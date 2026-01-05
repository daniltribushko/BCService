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
    void findByNameTest() {
        Country country = new Country("Find By Name Test Country");

        countryRepository.save(country);

        Region region1 = new Region("Test DisTrIcr", country);
        Region region2 = new Region("TeSt", country);
        Region region3 = new Region("region", country);

        regionRepository.saveAll(List.of(region1, region2, region3));

        List<Region> regions1 = regionRepository.findAll(NameSpecification.byNameWithFullTextSearch("tE"));
        List<Region> regions2 = regionRepository.findAll(NameSpecification.byNameWithFullTextSearch("dIs"));
        List<Region> regions3 = regionRepository.findAll(NameSpecification.byNameWithFullTextSearch("GiON"));
        List<Region> regions4 = regionRepository.findAll(NameSpecification.byNameWithFullTextSearch("NoT fOUnd"));

        Assertions.assertEquals(2, regions1.size());
        Assertions.assertEquals(1, regions2.size());
        Assertions.assertEquals(1, regions3.size());
        Assertions.assertEquals(0, regions4.size());
    }

    @Test
    void existsByNameTest() {
        Country country = new Country("Exists By Name Test Country");

        countryRepository.save(country);

        Region region1 = new Region("ХМАО", country);
        Region region2 = new Region("Московская область", country);
        Region region3 = new Region("Краснодарский край", country);
    }
}
