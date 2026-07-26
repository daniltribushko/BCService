package ru.tdd.geo.integrations.application.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.bc.http.AlreadyExistsException;
import ru.tdd.bc.http.countries.CountryAlreadyExistsException;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;
import ru.tdd.geo.TestcontainersConfiguration;
import ru.tdd.geo.application.models.dto.geo.country.*;
import ru.tdd.geo.application.services.CountryService;
import ru.tdd.geo.database.repositories.CountryRepository;
import ru.tdd.geo.sql.InitCountriesSqlScripts;
import ru.tdd.geo.utils.CountryUtils;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Tribushko Danil
 * @since 07.01.2026
 * Набор интеграционных тестов сервиса по работе со странами
 */
@SpringBootTest
@Testcontainers
@InitCountriesSqlScripts
@Import(value = TestcontainersConfiguration.class)
@DisplayName("Интеграционный тест сервиса стран")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryServiceTest {

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryService countryService;

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() {
        CountryDTO countryDTO = countryService.create(new CreateCountryDTO("New Country"));
        long actualCount = countryRepository.count();

        Assertions.assertEquals("New Country", countryDTO.getName());
        Assertions.assertEquals(5, actualCount);
    }

    @Test
    @DisplayName("Неудачное создание - страна уже создана")
    void saveAlreadyExistsFailTest() {
        AlreadyExistsException actual = Assertions.assertThrows(
                AlreadyExistsException.class,
                () -> countryService.create(new CreateCountryDTO("Россия"))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(CountryAlreadyExistsException.getErrorText("Россия"), actual.getMessage());
    }

    private static Stream<Arguments> updateSuccessTest() {
        return Stream.of(
                arguments(named("Название 1", CountryUtils.COUNTRY_ID1), "СССР"),
                arguments(named("Название 2", CountryUtils.COUNTRY_ID2), "КНР"),
                arguments(named("Название 3", CountryUtils.COUNTRY_ID4), "Римская Империя")
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Удачное обновление")
    void updateSuccessTest(UUID id, String name) {

        CountryDTO actualUpdateName = countryService.update(
                id,
                new UpdateCountryDTO(name)
        );

        Assertions.assertEquals(id, actualUpdateName.getId());
        Assertions.assertEquals(name, actualUpdateName.getName());
    }

    @Test
    @DisplayName("Неудачное обновление - страна уже создана")
    void updateAlreadyExistsFail() {
        CountryAlreadyExistsException actual = Assertions.assertThrows(
                CountryAlreadyExistsException.class,
                () -> countryService.update(CountryUtils.COUNTRY_ID2, new UpdateCountryDTO("Россия"))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        Assertions.assertEquals(CountryAlreadyExistsException.getErrorText("Россия"), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() {
        countryService.delete(CountryUtils.COUNTRY_ID2);
        long actualCount = countryRepository.count();

        Assertions.assertEquals(3, actualCount);
    }

    @Test
    @DisplayName("Неудачное удаление - страна не найдена")
    void deleteNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();
        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> countryService.delete(countryId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), actual.getMessage());
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void findByIdSuccessTest() {
        CountryDetailsDTO actual = countryService.getById(CountryUtils.COUNTRY_ID3);

        Assertions.assertEquals(CountryUtils.COUNTRY_ID3, actual.getId());
        Assertions.assertEquals("Франция", actual.getName());
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - страна не найдена")
    void findByIdNotFoundFailTest() {
        UUID countryId = UUID.randomUUID();
        CountryByIdNotFoundException actual = Assertions.assertThrows(
                CountryByIdNotFoundException.class,
                () -> countryService.getById(countryId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
        Assertions.assertEquals(CountryByIdNotFoundException.getErrorText(countryId), actual.getMessage());
    }

    private static Stream<Arguments> findAllTest() {
        return Stream.of(
                arguments(named("Поиск по названию 1", "ия"), 0, 100, 3),
                arguments(named("Поиск по названию 2", "КиТаЙ"), 0, 100, 1),
                arguments(named("Поиск по названию 3", "ИТА"), 0, 100, 2),
                arguments(named("Поиск с пустым названием", ""), 0, 100, 4),
                arguments(named("Поиск без названия", null), 0, 100, 4),
                arguments(named("Пагинация 1", null), 2, 10, 0),
                arguments(named("Пагинация 2", null), 1, 3, 1)
        );
    }

    @MethodSource
    @ParameterizedTest(name = "{0}")
    @DisplayName("Полнотекстовый поиск по странам")
    void findAllTest(String name, int page, int perPage, int expectedSize) {
        CountryListData countries = countryService.getAll(name, page, perPage);

        Assertions.assertEquals(expectedSize, countries.getData().size());
    }
}
