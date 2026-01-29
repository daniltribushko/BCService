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
import ru.tdd.geo.application.models.dto.geo.region.CreateRegionDTO;
import ru.tdd.geo.application.models.dto.geo.region.RegionDTO;
import ru.tdd.geo.application.models.dto.geo.region.RegionDetailsDTO;
import ru.tdd.geo.application.models.dto.geo.region.UpdateRegionDTO;
import ru.tdd.geo.application.models.exceptions.geo.country.CountryByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionAlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.services.imp.RegionServiceImp;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Tribushko Danil
 * @since 08.01.2026
 * Набор тестов сервиса по работе с регионами
 */
@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private RegionServiceImp regionServiceImp;

    @Test
    void saveSuccessTest() {
        Country country = new Country("Create Region Test Country");
        country.setId(UUID.randomUUID());

        Mockito.when(regionRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(countryRepository.findById(country.getId())).thenReturn(Optional.of(country));
        RegionDTO actual = regionServiceImp.create(new CreateRegionDTO("New Region", country.getId()));

        Mockito.verify(regionRepository).save(any(Region.class));
        Assertions.assertEquals("New Region", actual.getName());
        Assertions.assertEquals("Create Region Test Country", actual.getCountry().getName());
    }

    @Test
    void saveAlreadyExistsFailTest() {
        Mockito.when(regionRepository.exists(any(Specification.class))).thenReturn(true);

        RegionAlreadyExistsException actual = Assertions.assertThrows(
                RegionAlreadyExistsException.class, () -> regionServiceImp.create(
                        new CreateRegionDTO("Test Region", UUID.randomUUID())
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным названием и страной уже создан", actual.getMessage());
    }

    @Test
    void saveCountryNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();

        Mockito.when(regionRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> regionServiceImp.create(new CreateRegionDTO("Test Country", countryId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void updateSuccessTest() {
        Country country1 = new Country("Russia");
        country1.setId(UUID.randomUUID());

        Country country2 = new Country("China");
        country2.setId(UUID.randomUUID());

        Region region1 = new Region("Moscow oblast", country1);
        region1.setId(UUID.randomUUID());

        Region region2 = new Region("Anhui", country2);
        region2.setId(UUID.randomUUID());

        Mockito.when(regionRepository.findById(region1.getId())).thenReturn(Optional.of(region1));
        Mockito.when(regionRepository.findById(region2.getId())).thenReturn(Optional.of(region2));

        Mockito.when(countryRepository.findById(country1.getId())).thenReturn(Optional.of(country1));
        Mockito.when(countryRepository.findById(country2.getId())).thenReturn(Optional.of(country2));

        Mockito.when(regionRepository.exists(any(Specification.class))).thenReturn(false);

        RegionDTO actual1 = regionServiceImp.update(
                region1.getId(),
                new UpdateRegionDTO("Chelyabinsk region", country2.getId())
        );

        RegionDTO actual2 = regionServiceImp.update(
                region2.getId(),
                new UpdateRegionDTO(null, country1.getId())
        );

        RegionDTO actual3 = regionServiceImp.update(
                region2.getId(),
                new UpdateRegionDTO("Updated Region", null)
        );

        Assertions.assertEquals(region1.getId(), actual1.getId());
        Assertions.assertEquals("Chelyabinsk region", actual1.getName());
        Assertions.assertEquals(country2.getId(), actual1.getCountry().getId());

        Assertions.assertEquals(region2.getId(), actual2.getId());
        Assertions.assertEquals("Anhui", actual2.getName());
        Assertions.assertEquals(country1.getId(), actual2.getCountry().getId());

        Assertions.assertEquals(region2.getId(), actual3.getId());
        Assertions.assertEquals("Updated Region", actual3.getName());
        Assertions.assertEquals(country1.getId(), actual3.getCountry().getId());
    }

    @Test
    void updateRegionNotFoundFailTest() {
        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> regionServiceImp.update(UUID.randomUUID(), new UpdateRegionDTO("Not Found Region", UUID.randomUUID()))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void updateRegionCountryNotFoundFailTest() {
        Country country = new Country("Test Country");
        country.setId(UUID.randomUUID());

        Region region = new Region("Test Region", country);
        region.setId(UUID.randomUUID());

        UUID countryId = UUID.randomUUID();

        Mockito.when(regionRepository.findById(region.getId())).thenReturn(Optional.of(region));
        Mockito.when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> regionServiceImp.update(region.getId(), new UpdateRegionDTO(null, countryId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void updateAlreadyExistsFailTest() {
        Mockito.when(regionRepository.findById(any(UUID.class)))
                .thenReturn(
                        Optional.of(
                                new Region("Test Region",
                                        new Country("Test Country")
                                )
                        )
                );

        Mockito.when(regionRepository.exists(any(Specification.class)))
                .thenReturn(true);

        RegionAlreadyExistsException actual = Assertions.assertThrows(
                RegionAlreadyExistsException.class,
                () -> regionServiceImp.update(UUID.randomUUID(), new UpdateRegionDTO(null, null))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным названием и страной уже создан", actual.getMessage());
    }

    @Test
    void deleteSuccessTest() {
        UUID regionId = UUID.randomUUID();
        Region region = new Region("Region For Delete", new Country());
        region.setId(regionId);

        Mockito.when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));

        regionServiceImp.delete(regionId);

        Mockito.verify(regionRepository).delete(region);
    }

    @Test
    void deleteNotFoundFailTest() {
        UUID regionId = UUID.randomUUID();

        Mockito.when(regionRepository.findById(regionId)).thenReturn(Optional.empty());

        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> regionServiceImp.delete(regionId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void findByIdSuccessTest() {
        UUID regionId = UUID.randomUUID();
        Region region = new Region("Test Region", new Country());
        region.setId(regionId);

        Mockito.when(regionRepository.findById(regionId)).thenReturn(Optional.of(region));

        RegionDetailsDTO actual = regionServiceImp.getById(regionId);

        Assertions.assertEquals(regionId, actual.getId());
        Assertions.assertEquals("Test Region", region.getName());
    }

    @Test
    void findByIdNotFoundTest() {
        UUID regionId = UUID.randomUUID();

        Mockito.when(regionRepository.findById(regionId)).thenReturn(Optional.empty());

        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> regionServiceImp.getById(regionId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным идентификатором не найден", actual.getMessage());
    }

}
