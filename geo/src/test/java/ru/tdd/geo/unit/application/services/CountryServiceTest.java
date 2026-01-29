package ru.tdd.geo.unit.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import ru.tdd.geo.application.models.dto.geo.country.*;
import ru.tdd.geo.application.models.exceptions.AlreadyExistsException;
import ru.tdd.geo.application.models.exceptions.NotFoundException;
import ru.tdd.geo.application.services.imp.CountryServiceImp;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.database.specifications.NameSpecification;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Tribushko Danil
 * @since 06.01.2026
 * Набор тестов сервиса по работе со странами
 */
@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryServiceImp countryServiceImp;

    @Test
    void saveSuccessTest() {
        Mockito.when(countryRepository.exists(any(Specification.class))).thenReturn(false);

        CountryDTO actual = countryServiceImp.create(
                new CreateCountryDTO("Test Create Country 1")
        );

        Mockito.verify(countryRepository).exists(any(Specification.class));
        Mockito.verify(countryRepository).save(any(Country.class));
        Assertions.assertEquals("Test Create Country 1", actual.getName());
    }

    @Test
    void saveAlreadyExistsFailTest() {
        Mockito.when(countryRepository.exists(any(Specification.class))).thenReturn(true);

        AlreadyExistsException actual = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> countryServiceImp.create(new CreateCountryDTO(""))
        );

        Mockito.verify(countryRepository).exists(any(Specification.class));
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным названием уже создана", actual.getMessage());
    }

    @Test
    void updateSuccessTest() {
        Country country = new Country("Country For Update");
        UUID id = UUID.randomUUID();
        country.setId(id);

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.of(country));
        Mockito.when(countryRepository.exists(any(Specification.class))).thenReturn(false);

        CountryDTO actual = countryServiceImp.update(id, new UpdateCountryDTO("New Country Name"));

        Mockito.verify(countryRepository).findById(id);
        Mockito.verify(countryRepository).exists(any(Specification.class));
        Mockito.verify(countryRepository).save(any(Country.class));
        Assertions.assertEquals(id, actual.getId());
        Assertions.assertEquals("New Country Name", actual.getName());
    }

    @Test
    void updateNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException actual = Assertions.assertThrows(
                NotFoundException.class,
                () -> countryServiceImp.update(id, new UpdateCountryDTO("Not Found Country"))
        );

        Mockito.verify(countryRepository).findById(id);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void updateAlreadyExistsFailTest() {
        Country country = new Country("Already Exists Country");
        UUID id = UUID.randomUUID();

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.of(country));
        Mockito.when(countryRepository.exists(any(Specification.class))).thenReturn(true);

        AlreadyExistsException actual = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> countryServiceImp.update(id, new UpdateCountryDTO("New Country"))
        );

        Mockito.verify(countryRepository).exists(any(Specification.class));
        Mockito.verify(countryRepository).findById(id);
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным названием уже создана", actual.getMessage());
    }

    @Test
    void deleteSuccessTest() {
        Country country = new Country("Country For Delete");
        UUID id = UUID.randomUUID();

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.of(country));

        countryServiceImp.delete(id);

        Mockito.verify(countryRepository).findById(id);
        Mockito.verify(countryRepository).delete(country);
    }

    @Test
    void deleteNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException actual = Assertions.assertThrows(
                NotFoundException.class,
                () -> countryServiceImp.delete(id)
        );

        Mockito.verify(countryRepository).findById(id);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Страна с указанным идентификатором не найдена", actual.getMessage());
    }

    @Test
    void findByIdSuccessTest() {
        UUID id = UUID.randomUUID();

        Country country = new Country("Find By Id Country");
        country.setId(id);

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.of(country));

        CountryDetailsDTO actual = countryServiceImp.getById(id);

        Mockito.verify(countryRepository).findById(id);
        Assertions.assertEquals(id, actual.getId());
        Assertions.assertEquals("Find By Id Country", actual.getName());
    }

    @Test
    void findByIdNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException actual = Assertions.assertThrows(
                NotFoundException.class,
                () -> countryServiceImp.getById(id)
        );

        Mockito.verify(countryRepository).findById(id);
        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
    }

    @Test
    void findAllTest() {
        PageImpl<Country> countries = new PageImpl<>(
                List.of(
                        new Country("Find All Country 1"),
                        new Country("Find All Country 2")
                ),
                PageRequest.of(0, 2),
                2
        );

        Mockito.when(
                        countryRepository.findAll(
                                any(Specification.class),
                                any(PageRequest.class)
                        )
                )
                .thenReturn(countries);

        CountriesDTO actual = countryServiceImp.getAll("test", 0, 2);

        Mockito.verify(countryRepository).findAll(any(Specification.class), any(PageRequest.class));
        Assertions.assertEquals(2, actual.getData().size());
    }
}
