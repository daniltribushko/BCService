package ru.tdd.geo.unit.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import ru.tdd.bc.http.AlreadyExistsException;
import ru.tdd.bc.http.NotFoundException;
import ru.tdd.bc.http.countries.CountryAlreadyExistsException;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.geo.application.mappers.CountryMapper;
import ru.tdd.geo.application.models.dto.geo.country.CountryDTO;
import ru.tdd.geo.application.models.dto.geo.country.CountryDetailsDTO;
import ru.tdd.geo.application.models.dto.geo.country.CreateCountryDTO;
import ru.tdd.geo.application.models.dto.geo.country.UpdateCountryDTO;
import ru.tdd.geo.application.services.imp.CountryServiceImp;
import ru.tdd.geo.application.services.imp.kafka.CountryKafkaService;
import ru.tdd.geo.database.entities.Country;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.kafka_core.entities.OutboxEventType;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Tribushko Danil
 * @since 06.01.2026
 * Набор тестов сервиса по работе со странами
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тест сервиса стран")
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @Mock
    private CountryKafkaService countryKafkaService;

    @InjectMocks
    private CountryServiceImp countryServiceImp;

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        UUID countryId = UUID.randomUUID();

        Mockito.when(countryRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(countryMapper.toDto(any(Country.class)))
                .thenReturn(new CountryDTO(countryId, "Россия"));

        CountryDTO actual = countryServiceImp.create(
                new CreateCountryDTO("Россия")
        );

        Mockito.verify(countryRepository).exists(any(Specification.class));
        Mockito.verify(countryRepository).save(any(Country.class));
        Mockito.verify(countryKafkaService).send(any(OutboxEventType.class), any(Country.class));
        Assertions.assertEquals(countryId, actual.getId());
        Assertions.assertEquals("Россия", actual.getName());
    }

    @Test
    @DisplayName("Неудачное создание - страна уже создана")
    void saveAlreadyExistsFailTest() {
        Mockito.when(countryRepository.exists(any(Specification.class))).thenReturn(true);

        AlreadyExistsException actual = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> countryServiceImp.create(new CreateCountryDTO("Россия"))
        );

        Mockito.verify(countryRepository).exists(any(Specification.class));
        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(CountryAlreadyExistsException.getErrorText("Россия"), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() {
        Country country = new Country("КНР");
        UUID id = UUID.randomUUID();
        country.setId(id);

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.of(country));
        Mockito.when(countryRepository.exists(any(Specification.class))).thenReturn(false);
        Mockito.when(countryMapper.toDto(country))
                .thenReturn(
                        new CountryDTO(
                                id,
                                "Китай"
                        )
                );

        CountryDTO actual = countryServiceImp.update(id, new UpdateCountryDTO("Китай"));

        Mockito.verify(countryRepository).findById(id);
        Mockito.verify(countryRepository).exists(any(Specification.class));
        Mockito.verify(countryRepository).save(any(Country.class));
        Mockito.verify(countryKafkaService).send(any(OutboxEventType.class), any(Country.class));
        Assertions.assertEquals(id, actual.getId());
        Assertions.assertEquals("Китай", actual.getName());
    }

    @Test
    @DisplayName("Не удачное обновление - страна не найдена")
    void updateNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException actual = Assertions.assertThrows(
                NotFoundException.class,
                () -> countryServiceImp.update(id, new UpdateCountryDTO("Испания"))
        );

        Mockito.verify(countryRepository).findById(id);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(id), actual.getMessage());
    }

    @Test
    @DisplayName("Неудачное обновление страна уже создана")
    void updateAlreadyExistsFailTest() {
        Country country = new Country("Россия");
        UUID id = UUID.randomUUID();

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.of(country));
        Mockito.when(countryRepository.exists(any(Specification.class))).thenReturn(true);

        AlreadyExistsException actual = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> countryServiceImp.update(id, new UpdateCountryDTO("Россия"))
        );

        Mockito.verify(countryRepository).exists(any(Specification.class));
        Mockito.verify(countryRepository).findById(id);
        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(CountryAlreadyExistsException.getErrorText("Россия"), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        Country country = new Country("Country For Delete");
        UUID id = UUID.randomUUID();

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.of(country));

        countryServiceImp.delete(id);

        Mockito.verify(countryRepository).findById(id);
        Mockito.verify(countryRepository).delete(country);
        Mockito.verify(countryKafkaService).send(any(OutboxEventType.class), any(Country.class));
    }

    @Test
    @DisplayName("Неудачное удаление")
    void deleteNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException actual = Assertions.assertThrows(
                NotFoundException.class,
                () -> countryServiceImp.delete(id)
        );

        Mockito.verify(countryRepository).findById(id);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(id), actual.getMessage());
    }

    @Test
    @DisplayName("Удачный поиск по идентификатору")
    void findByIdSuccessTest() {
        UUID id = UUID.randomUUID();

        Country country = new Country("Россия");
        country.setId(id);

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.of(country));
        Mockito.when(countryMapper.toDetailsDto(country)).thenReturn(
                new CountryDetailsDTO(id, "Россия", null)
        );

        CountryDetailsDTO actual = countryServiceImp.getById(id);

        Mockito.verify(countryRepository).findById(id);
        Assertions.assertEquals(id, actual.getId());
        Assertions.assertEquals("Россия", actual.getName());
    }

    @Test
    @DisplayName("Неудачный поиск по идентификатору - страна не найдена")
    void findByIdNotFoundFailTest() {
        UUID id = UUID.randomUUID();

        Mockito.when(countryRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException actual = Assertions.assertThrows(
                NotFoundException.class,
                () -> countryServiceImp.getById(id)
        );

        Mockito.verify(countryRepository).findById(id);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }
}
