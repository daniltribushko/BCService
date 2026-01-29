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
import ru.tdd.geo.application.models.dto.geo.city.CityDTO;
import ru.tdd.geo.application.models.dto.geo.city.CityDetailsDTO;
import ru.tdd.geo.application.models.dto.geo.city.CreateCityDTO;
import ru.tdd.geo.application.models.dto.geo.city.UpdateCityDTO;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityAlreadyExistException;
import ru.tdd.geo.application.models.exceptions.geo.cities.CityByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.country.CountryByIdNotFoundException;
import ru.tdd.geo.application.models.exceptions.geo.region.RegionByIdNotFoundException;
import ru.tdd.geo.application.services.imp.CityServiceImp;
import ru.tdd.geo.database.entities.City;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.entities.Region;
import ru.tdd.geo.database.repositories.CityRepository;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.repositories.RegionRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Tribushko Danil
 * @since 17.01.2026
 * Набор тестов сервиса по работе с горадами
 */
@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityServiceImp cityService;

    @Test
    void createSuccessTest() {
        Country country = new Country("Test Country");
        country.setId(UUID.randomUUID());
        Region region = new Region("Test Region", country);
        region.setId(UUID.randomUUID());

        Mockito.when(cityRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(regionRepository.findById(region.getId())).thenReturn(Optional.of(region));

        CityDTO actual = cityService.create(new CreateCityDTO("New City", region.getId(), null));

        Mockito.verify(cityRepository).save(any(City.class));
        Assertions.assertEquals("New City", actual.getName());
        Assertions.assertEquals(region.getId(), actual.getRegion().getId());
        Assertions.assertEquals(country.getId(), actual.getCountry().getId());
    }

    @Test
    void createAlreadyExistsFailTest() {
        UUID regionId = UUID.randomUUID();

        Mockito.when(cityRepository.exists(any(Specification.class))).thenReturn(true);
        Mockito.when(regionRepository.findById(regionId)).thenReturn(
                Optional.of(new Region("Region", new Country("Country")))
        );

        CityAlreadyExistException actual = Assertions.assertThrows(
                CityAlreadyExistException.class,
                () -> cityService.create(
                        new CreateCityDTO(
                                "Already Exists City",
                                regionId,
                                UUID.randomUUID()
                        )
                )
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Город с указанным названием, страной, регионом уже создан", actual.getMessage());
    }

    @Test
    void createRegionNotFoundFailTest() {
        UUID regionId = UUID.randomUUID();

        Mockito.when(regionRepository.findById(regionId)).thenReturn(Optional.empty());

        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> cityService.create(new CreateCityDTO("Test City", regionId, UUID.randomUUID()))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void createCountryNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();

        Mockito.when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> cityService.create(new CreateCityDTO("Test City", null, countryId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void updateSuccessTest() {
        Country country1 = new Country("Test Country 1");
        Country country2 = new Country("Test Country 2");
        country1.setId(UUID.randomUUID());
        country2.setId(UUID.randomUUID());

        Region region1 = new Region("Test Region 1", country1);
        Region region2 = new Region("Test Region 2", country2);
        region1.setId(UUID.randomUUID());
        region2.setId(UUID.randomUUID());

        City city = new City("Test City", null, country1);
        UUID cityId = city.getId();
        city.setId(cityId);

        Mockito.when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
        Mockito.when(countryRepository.findById(country2.getId())).thenReturn(Optional.of(country2));
        Mockito.when(regionRepository.findById(region1.getId())).thenReturn(Optional.of(region1));
        Mockito.when(regionRepository.findById(region2.getId())).thenReturn(Optional.of(region2));
        Mockito.when(cityRepository.exists(any(Specification.class))).thenReturn(false);

        CityDTO actual1 = cityService.update(cityId, new UpdateCityDTO("New City", null, null));
        CityDTO actual2 = cityService.update(cityId, new UpdateCityDTO(null, region1.getId(), null));
        CityDTO actual3 = cityService.update(cityId, new UpdateCityDTO(null, null, country2.getId()));
        CityDTO actual4 = cityService.update(cityId, new UpdateCityDTO(null, region2.getId(), null));

        Assertions.assertEquals("New City", actual1.getName());
        Assertions.assertEquals(region1.getId(), actual2.getRegion().getId());
        Assertions.assertEquals(country2.getId(), actual3.getCountry().getId());
        Assertions.assertEquals(region2.getId(), actual4.getRegion().getId());
    }

    @Test
    void updateCityNotFoundFailTest() {
        UUID cityId = UUID.randomUUID();
        Mockito.when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> cityService.update(cityId, new UpdateCityDTO("New City", null, null))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Город с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void updateRegionNotFoundFailTest() {
        Country country = new Country("TestCountry");
        country.setId(UUID.randomUUID());

        City city = new City("Test City", null, country);
        city.setId(UUID.randomUUID());

        UUID regionId = UUID.randomUUID();

        Mockito.when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
        Mockito.when(regionRepository.findById(regionId)).thenReturn(Optional.empty());

        RegionByIdNotFoundException actual = Assertions.assertThrows(
                RegionByIdNotFoundException.class,
                () -> cityService.update(city.getId(), new UpdateCityDTO(null, regionId, null))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Регион с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void updateCountryNotFoundFailTest() {
        City city = new City("Test City", null, new Country("Country"));
        city.setId(UUID.randomUUID());

        UUID countryId = UUID.randomUUID();

        Mockito.when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
        Mockito.when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> cityService.update(city.getId(), new UpdateCityDTO(null, null, countryId))
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void updateAlreadyExistsException() {
        Country country = new Country("Test Country");
        country.setId(UUID.randomUUID());

        City city = new City("Test City", null, country);
        city.setId(UUID.randomUUID());

        Mockito.when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));
        Mockito.when(cityRepository.exists(any(Specification.class))).thenReturn(true);

        CityAlreadyExistException actual = Assertions.assertThrows(
                CityAlreadyExistException.class,
                () -> cityService.update(city.getId(), new UpdateCityDTO("City", null, null))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Город с указанным названием, страной, регионом уже создан", actual.getMessage());
    }

    @Test
    void getByIdSuccessTest() {
        Country country = new Country("Test Country");
        country.setId(UUID.randomUUID());

        City city = new City("Test City", null, country);
        city.setId(UUID.randomUUID());

        Mockito.when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));

        CityDetailsDTO actual = cityService.getById(city.getId());

        Assertions.assertEquals(city.getId(), actual.getId());
        Assertions.assertEquals(city.getName(), actual.getName());
    }

    @Test
    void getByIdNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(cityRepository.findById(id)).thenReturn(Optional.empty());

        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> cityService.getById(id)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Город с указанным идентификатором не найден", actual.getMessage());
    }

    @Test
    void deleteSuccessTest() {
        City city = new City("City For Delete", null, null);
        city.setId(UUID.randomUUID());

        Mockito.when(cityRepository.findById(city.getId())).thenReturn(Optional.of(city));

        cityService.delete(city.getId());

        Mockito.verify(cityRepository).findById(city.getId());
        Mockito.verify(cityRepository).delete(city);
    }

    @Test
    void deleteNotFoundTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(cityRepository.findById(id)).thenReturn(Optional.empty());

        CityByIdNotFoundException actual = Assertions.assertThrows(
                CityByIdNotFoundException.class,
                () -> cityService.delete(id)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Город с указанным идентификатором не найден", actual.getMessage());
    }
}
