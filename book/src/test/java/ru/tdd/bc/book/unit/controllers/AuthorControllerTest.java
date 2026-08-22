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
import ru.tdd.bc.book.application.dto.authors.AuthorDTO;
import ru.tdd.bc.book.application.dto.authors.CreateAuthorDTO;
import ru.tdd.bc.book.application.dto.authors.UpdateAuthorDTO;
import ru.tdd.bc.book.application.dto.countries.CountryDTO;
import ru.tdd.bc.book.application.exceptions.AuthorByIdNotFoundException;
import ru.tdd.bc.book.application.services.AuthorService;
import ru.tdd.bc.book.controller.rest_controllers.imp.AuthorControllerImp;
import ru.tdd.bc.book.utils.CountryUtils;
import ru.tdd.bc.controller.ApiExceptionControllerAdvice;
import ru.tdd.bc.controller.ValidationControllerAdvice;
import ru.tdd.bc.http.countries.CountryByIdNotFoundException;

import java.time.LocalDate;
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
@DisplayName("Unit тест контроллера для работы с авторами")
class AuthorControllerTest {

    private static final String BASE_URL = "/books/authors";

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorControllerImp authorController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authorController)
                .setControllerAdvice(new ValidationControllerAdvice(objectMapper), new ApiExceptionControllerAdvice())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Удачное создание автора")
    void saveSuccessTest() throws Exception {
        UUID authorId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Mockito.when(authorService.create(any(CreateAuthorDTO.class)))
                .thenReturn(
                        new AuthorDTO(
                                authorId,
                                "Иванов",
                                "Иванович",
                                "Иван",
                                new CountryDTO(countryId, "Россия"),
                                null,
                                null,
                                null
                        )
                );

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsBytes(
                                                new CreateAuthorDTO(
                                                        "Иванов",
                                                        "Иванович",
                                                        "Иван",
                                                        countryId,
                                                        null
                                                )
                                        )
                                )
                ).andExpect(status().isCreated())
                .andExpect(
                        jsonPath(
                                "$.id",
                                is(authorId.toString())
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.lastName",
                                is("Иванов")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.middleName",
                                is("Иванович")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.firstName",
                                is("Иван")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.country.id",
                                is(countryId.toString())
                        )
                );
    }

    @Test
    @DisplayName("Неудачное создание - автор не найден")
    void saveCountryNotFoundFailTest() throws Exception {
        UUID countryId = UUID.randomUUID();

        Mockito.when(authorService.create(any(CreateAuthorDTO.class)))
                .thenThrow(new CountryByIdNotFoundException(countryId));

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsBytes(
                                                new CreateAuthorDTO("Иванов", null, "Иван", countryId, null)
                                        )
                                )
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(CountryByIdNotFoundException.getErrorText(countryId))));
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() throws Exception {
        UUID authorId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Mockito.when(authorService.update(any(UUID.class), any(UpdateAuthorDTO.class)))
                .thenReturn(
                        new AuthorDTO(
                                authorId,
                                "Иванов",
                                "Иванович",
                                "Иван",
                                new CountryDTO(countryId, "Россия"),
                                LocalDate.of(1999, 1, 1),
                                null,
                                null
                        )
                );

        mockMvc.perform(
                        put(BASE_URL + "/" + authorId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsBytes(
                                                new UpdateAuthorDTO(
                                                        "Иванов",
                                                        "Иванович",
                                                        "Иван",
                                                        countryId,
                                                        null
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(authorId.toString())))
                .andExpect(jsonPath("$.lastName", is("Иванов")))
                .andExpect(jsonPath("$.middleName", is("Иванович")))
                .andExpect(jsonPath("$.firstName", is("Иван")))
                .andExpect(jsonPath("$.country.id", is(countryId.toString())));
    }

    @Test
    @DisplayName("Неудачное обновление - автор не найден")
    void updateAuthorNotFoundFailTest() throws Exception {
        UUID authorId = UUID.randomUUID();

        Mockito.when(authorService.update(any(UUID.class), any(UpdateAuthorDTO.class)))
                .thenThrow(new AuthorByIdNotFoundException());

        mockMvc.perform(
                        put(BASE_URL + "/" + authorId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(new UpdateAuthorDTO()))
                )
                .andExpect(jsonPath("$.message", is(AuthorByIdNotFoundException.getErrorText())));
    }

    @Test
    @DisplayName("Неудачное обновление - страна не найдена")
    void updateCountryNotFoundFailTest() throws Exception {
        UUID authorId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        Mockito.when(authorService.update(any(UUID.class), any(UpdateAuthorDTO.class)))
                .thenThrow(new CountryByIdNotFoundException(countryId));

        mockMvc.perform(
                        put(BASE_URL + "/" + authorId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(new UpdateAuthorDTO(null, null, null, countryId, null)))
                )
                .andExpect(jsonPath("$.message", is(CountryByIdNotFoundException.getErrorText(countryId))));
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() throws Exception {
        UUID authorId = UUID.randomUUID();

        Mockito.when(authorService.getById(authorId))
                .thenReturn(
                        new AuthorDTO(
                                authorId,
                                "Иванов",
                                "Иванович",
                                "Иван",
                                new CountryDTO(CountryUtils.COUNTRY_ID1, null),
                                LocalDate.of(1999, 1, 1),
                                null,
                                null
                        )
                );

        mockMvc.perform(get(BASE_URL + "/" + authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(authorId.toString())))
                .andExpect(jsonPath("$.lastName", is("Иванов")))
                .andExpect(jsonPath("$.middleName", is("Иванович")))
                .andExpect(jsonPath("$.firstName", is("Иван")))
                .andExpect(jsonPath("$.country.id", is(CountryUtils.COUNTRY_ID1.toString())));
    }

    @Test
    @DisplayName("Неудачное получение по идентификаторов - автор не найден")
    void getByIdNotFoundFailTest() throws Exception {
        UUID authorId = UUID.randomUUID();

        Mockito.when(authorService.getById(authorId))
                .thenThrow(new AuthorByIdNotFoundException());

        mockMvc.perform(get(BASE_URL + "/" + authorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(AuthorByIdNotFoundException.getErrorText())));
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Неудачное удаление - издатель не найден")
    void deleteNotFoundFailTest() throws Exception {
        UUID authorId = UUID.randomUUID();

        Mockito.doThrow(new AuthorByIdNotFoundException())
                .when(authorService)
                .delete(authorId);

        mockMvc.perform(delete(BASE_URL + "/" + authorId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(AuthorByIdNotFoundException.getErrorText())));
    }
}
