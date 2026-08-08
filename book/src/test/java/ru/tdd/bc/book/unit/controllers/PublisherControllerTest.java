package ru.tdd.bc.book.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.dto.publisher.CreatePublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.UpdatePublisherDTO;
import ru.tdd.bc.book.application.exceptions.PublisherAlreadyExistsException;
import ru.tdd.bc.book.application.exceptions.PublisherByIdNotFoundException;
import ru.tdd.bc.book.application.services.PublisherService;
import ru.tdd.bc.book.controller.rest_controllers.imp.PublisherControllerImp;
import ru.tdd.bc.book.utils.CountryUtils;
import ru.tdd.bc.controller.ApiExceptionControllerAdvice;
import ru.tdd.bc.controller.ValidationControllerAdvice;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 07.08.2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit тесты контроллера для работы с издателями")
public class PublisherControllerTest {

    public static final String BASE_URL = "/books/publishers";

    @Mock
    private PublisherService publisherService;

    @InjectMocks
    private PublisherControllerImp publisherController;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setMockMvc() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(publisherController)
                .setControllerAdvice(
                        new ValidationControllerAdvice(objectMapper),
                        new ApiExceptionControllerAdvice()
                )
                .build();
    }

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() throws Exception {
        UUID publisherId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Mockito.when(publisherService.create(any(CreatePublisherDTO.class)))
                .thenReturn(
                        new PublisherDTO(
                                publisherId,
                                "Вече",
                                "https://veche.ru",
                                new CountryDTO(countryId, null),
                                null,
                                null
                        )
                );

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new CreatePublisherDTO("АСТ", null, UUID.randomUUID())
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(publisherId.toString())))
                .andExpect(jsonPath("$.name", is("Вече")))
                .andExpect(jsonPath("$.url", is("https://veche.ru")))
                .andExpect(jsonPath("$.country.id", is(countryId.toString())));
    }

    @Test
    @DisplayName("Неудачное создание - страна не найдена")
    void saveCountryNotFoundFailTest() throws Exception {
        UUID countryId = UUID.randomUUID();

        Mockito.when(publisherService.create(any(CreatePublisherDTO.class)))
                .thenThrow(new CountryByIdNotFoundException(countryId));

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(new CreatePublisherDTO("Вече", null, countryId)))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(CountryByIdNotFoundException.getErrorText(countryId))));
    }

    @Test
    @DisplayName("Неудачное создание издатель уже создан")
    void saveAlreadyExistsFailTest() throws Exception {
        Mockito.when(publisherService.create(any(CreatePublisherDTO.class)))
                .thenThrow(new PublisherAlreadyExistsException());

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new CreatePublisherDTO("АСТ", null, UUID.randomUUID())
                                        )
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(PublisherAlreadyExistsException.getErrorText())));
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() throws Exception {
        UUID publisherId = UUID.randomUUID();

        Mockito.when(publisherService.update(any(UUID.class), any(UpdatePublisherDTO.class)))
                .thenReturn(
                        new PublisherDTO(
                                publisherId,
                                "АСТ",
                                "https://ast.ru",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, "Россия"),
                                null,
                                null
                        )
                );

        mockMvc.perform(
                        put(BASE_URL + "/" + publisherId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new UpdatePublisherDTO(
                                                        "АСТ",
                                                        "https://ast.ru",
                                                        CountryUtils.COUNTRY_ID1
                                                )
                                        )
                                )
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(publisherId.toString())))
                .andExpect(jsonPath("$.name", is("АСТ")))
                .andExpect(jsonPath("$.url", is("https://ast.ru")))
                .andExpect(jsonPath("$.country.id", is(CountryUtils.COUNTRY_ID1.toString())));
    }

    @Test
    @DisplayName("Неудачное обновление - издатель не найден")
    void updatePublisherNotFoundFailTest() throws Exception {
        Mockito.when(publisherService.update(any(UUID.class), any(UpdatePublisherDTO.class)))
                .thenThrow(new PublisherByIdNotFoundException());

        mockMvc.perform(
                        put(BASE_URL + "/" + UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new UpdatePublisherDTO(
                                                        "АСТ",
                                                        "https://ast.ru",
                                                        CountryUtils.COUNTRY_ID1
                                                )
                                        )
                                )
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(PublisherByIdNotFoundException.getErrorText())));
    }

    @Test
    @DisplayName("Неудачное обновление - страна не найдена")
    void updateCountryNotFoundFailTest() throws Exception {
        UUID countryId = UUID.randomUUID();

        Mockito.when(publisherService.update(any(UUID.class), any(UpdatePublisherDTO.class)))
                .thenThrow(new CountryByIdNotFoundException(countryId));

        mockMvc.perform(
                        put(BASE_URL + "/" + UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new UpdatePublisherDTO(
                                                        null,
                                                        null,
                                                        countryId
                                                )
                                        )
                                )
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(CountryByIdNotFoundException.getErrorText(countryId))));
    }

    @Test
    @DisplayName("Неудачное обновление - издатель уже создан")
    void updateAlreadyExistsFailTest() throws Exception {
        Mockito.when(publisherService.update(any(UUID.class), any(UpdatePublisherDTO.class)))
                .thenThrow(new PublisherAlreadyExistsException());

        mockMvc.perform(
                        put(BASE_URL + "/" + UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new UpdatePublisherDTO(
                                                        "АСТ",
                                                        "https://ast.ru",
                                                        CountryUtils.COUNTRY_ID1
                                                )
                                        )
                                )
                ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(PublisherAlreadyExistsException.getErrorText())));
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() throws Exception {
        UUID publisherId = UUID.randomUUID();

        Mockito.when(publisherService.getById(publisherId))
                .thenReturn(
                        new PublisherDTO(
                                publisherId,
                                "Питер",
                                "https://www.piter.com",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                null,
                                null
                        )
                );

        mockMvc.perform(get(BASE_URL + "/" + publisherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(publisherId.toString())))
                .andExpect(jsonPath("$.name", is("Питер")))
                .andExpect(jsonPath("$.url", is("https://www.piter.com")))
                .andExpect(jsonPath("$.country.id", is(CountryUtils.COUNTRY_ID1.toString())));
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - издатель не найден")
    void getByIdNotFoundFailTest() throws Exception {
        UUID publisherId = UUID.randomUUID();

        Mockito.when(publisherService.getById(publisherId))
                .thenThrow(new PublisherByIdNotFoundException());

        mockMvc.perform(get(BASE_URL + "/" + publisherId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(PublisherByIdNotFoundException.getErrorText())));
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() throws Exception {
        UUID publisherId = UUID.randomUUID();

        mockMvc.perform(delete(BASE_URL + "/" + publisherId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Неудачное удаление - издатель не найден")
    void deleteNotFoundFailTest() throws Exception {
        UUID publisherId = UUID.randomUUID();

        Mockito.doThrow(new PublisherByIdNotFoundException())
                .when(publisherService)
                .delete(publisherId);

        mockMvc.perform(delete(BASE_URL + "/" + publisherId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(PublisherByIdNotFoundException.getErrorText())));
    }
}
