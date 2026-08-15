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
import ru.tdd.bc.book.application.exceptions.GenreAlreadyExistsException;
import ru.tdd.bc.book.application.exceptions.GenreByIdNotFoundException;
import ru.tdd.bc.book.application.services.GenreService;
import ru.tdd.bc.book.controller.rest_controllers.imp.GenreControllerImp;
import ru.tdd.bc.controller.ApiExceptionControllerAdvice;
import ru.tdd.bc.controller.ValidationControllerAdvice;
import ru.tdd.bc.dictionaries.dto.CreateDictionaryDto;
import ru.tdd.bc.dictionaries.dto.DictionaryDto;
import ru.tdd.bc.dictionaries.dto.UpdateDictionaryDto;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 15.08.2026
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit тесты контроллера жанров")
public class GenreControllerTest {

    private static final String BASE_URL = "/books/genres";

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreControllerImp genreController;

    @BeforeEach
    void setMockMvc() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(genreController)
                .setControllerAdvice(
                        new ValidationControllerAdvice(objectMapper),
                        new ApiExceptionControllerAdvice()
                )
                .build();
    }

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(genreService.create(any(CreateDictionaryDto.class)))
                .thenReturn(new DictionaryDto(id, "Автобиография"));

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new CreateDictionaryDto("Автобиография")
                                        )
                                )
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Автобиография")));
    }

    @Test
    @DisplayName("Неудачное создание - жанр уже создан")
    void saveAlreadyExistsFailTest() throws Exception {
        Mockito.when(genreService.create(any(CreateDictionaryDto.class)))
                .thenThrow(new GenreAlreadyExistsException());

        mockMvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(new CreateDictionaryDto("Стимпанк"))
                                )
                ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(GenreAlreadyExistsException.getErrorText())));
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(genreService.update(any(UUID.class), any(UpdateDictionaryDto.class)))
                .thenReturn(new DictionaryDto(id, "Автобиография"));

        mockMvc.perform(
                        put(BASE_URL + "/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(new CreateDictionaryDto("Автобиография"))
                                )
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Автобиография")));
    }

    @Test
    @DisplayName("Неудачное обновление - жанр не найден")
    void updateNotFoundFailTest() throws Exception {
        Mockito.when(genreService.update(any(UUID.class), any(UpdateDictionaryDto.class)))
                .thenThrow(new GenreByIdNotFoundException());

        mockMvc.perform(
                        put(BASE_URL + "/" + UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(new UpdateDictionaryDto("Автобиография"))
                                )
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(GenreByIdNotFoundException.getErrorText())));
    }

    @Test
    @DisplayName("Неудачное обновление - жанр уже создан")
    void updateAlreadyExistsFailTest() throws Exception {
        Mockito.when(genreService.update(any(UUID.class), any(UpdateDictionaryDto.class)))
                .thenThrow(new GenreAlreadyExistsException());

        mockMvc.perform(
                        put(BASE_URL + "/" + UUID.randomUUID())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(new UpdateDictionaryDto("Любовный роман"))
                                )
                ).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(GenreAlreadyExistsException.getErrorText())));
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(genreService.getById(id))
                .thenReturn(new DictionaryDto(id, "Научно-популярная литература"));

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Научно-популярная литература")));
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - жанр не найден")
    void getByIdNotFoundFailTest() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(genreService.getById(id))
                .thenThrow(new GenreByIdNotFoundException());

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(jsonPath("$.message", is(GenreByIdNotFoundException.getErrorText())));
    }

    @Test
    @DisplayName("Удачное удаление")
    void deleteSuccessTest() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Неудачное удаление - жанр не найден")
    void deleteNotFoundFailTest() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.doThrow(new GenreByIdNotFoundException())
                .when(genreService)
                .delete(id);

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(GenreByIdNotFoundException.getErrorText())));
    }
}
