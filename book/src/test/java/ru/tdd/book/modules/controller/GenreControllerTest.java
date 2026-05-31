package ru.tdd.book.modules.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tdd.book.controllers.dto.CreateGenreDto;
import ru.tdd.book.controllers.dto.GenreDto;
import ru.tdd.book.controllers.dto.UpdateGenreDto;
import ru.tdd.book.controllers.filters.JwtFilter;
import ru.tdd.book.controllers.rest_controllers.GenreControllerImp;
import ru.tdd.book.domain.exceptions.genre.GenreAlreadyExistsException;
import ru.tdd.book.domain.exceptions.genre.GenreByIdNotFoundException;
import ru.tdd.book.domain.services.genres.GenreService;
import ru.tdd.core.application.utils.UrlUtils;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 28.05.2026
 */
@WebMvcTest(controllers = GenreControllerImp.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Модульный тест контроллера по работе с жанрами")
public class GenreControllerTest {

    private static final String BASE_PATH = "/genre";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private JwtFilter jwtFilter;

    @Test
    @DisplayName("Удачное создание")
    void saveSuccessTest() throws Exception {
        var id = UUID.randomUUID();

        var createDto = new CreateGenreDto("Новый жанр");
        var expectedResponse = new GenreDto(id, "Новый жанр");

        Mockito.when(genreService.create(any(CreateGenreDto.class)))
                .thenReturn(expectedResponse);

        mockMvc.perform(
                        post(BASE_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(createDto))
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id").value(id.toString())
                )
                .andExpect(
                        jsonPath("$.name").value("Новый жанр")
                );
    }

    @Test
    @DisplayName("Неудачное создание - жанр уже создан")
    void saveAlreadyExistsFailTest() throws Exception {
        CreateGenreDto dto = new CreateGenreDto("Новый жанр");

        Mockito.when(genreService.create(any(CreateGenreDto.class)))
                .thenThrow(new GenreAlreadyExistsException(dto.getName()));

        mockMvc.perform(
                        post(BASE_PATH)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.message")
                                .value("Жанр: \"Новый жанр\" уже создан")
                );
    }

    @Test
    @DisplayName("Удачное обновление")
    void updateSuccessTest() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(genreService.update(any(UUID.class), any(UpdateGenreDto.class)))
                .thenReturn(new GenreDto(id, "Образовательная литература"));

        mockMvc.perform(
                        put(
                                UrlUtils.builder(BASE_PATH)
                                        .add(id)
                                        .build()
                        )
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(
                                        objectMapper.writeValueAsString(
                                                new UpdateGenreDto("Образовательная литература")
                                        )
                                )
                ).andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(id.toString())
                );

    }

    @Test
    @DisplayName("Неудачное обновление - жанр не найден")
    void updateNotFoundFailTest() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(genreService.update(any(UUID.class), any(UpdateGenreDto.class)))
                .thenThrow(new GenreByIdNotFoundException(id));

        mockMvc.perform(
                put(UrlUtils.builder(BASE_PATH).add(id).build())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new UpdateGenreDto()))
        )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("Жанр с идентификатором: \"%s\" не найден".formatted(id))
                );
    }

    @Test
    @DisplayName("Неудачное обновление - жанр уже создан")
    void updateAlreadyExistsFailTest() throws Exception {
        UpdateGenreDto dto = new UpdateGenreDto("Новый жанр");

        Mockito.when(genreService.update(any(UUID.class), any(UpdateGenreDto.class)))
                .thenThrow(new GenreAlreadyExistsException(dto.getName()));

        mockMvc.perform(
                put(UrlUtils.builder(BASE_PATH).add(UUID.randomUUID()).build())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.message")
                                .value("Жанр: \"Новый жанр\" уже создан")
                );
    }

    @Test
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() throws Exception {
        UUID id = UUID.randomUUID();

        GenreDto dto = new GenreDto(id, "Жанр 1");

        Mockito.when(genreService.getById(id))
                        .thenReturn(dto);

        mockMvc.perform(
                get(UrlUtils.builder(BASE_PATH).add(id).build())
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Жанр 1"));
    }

    @Test
    @DisplayName("Неудачное получение по идентификатору - жанр не найден")
    void getByIdnNotFoundFailTest() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(genreService.getById(id))
                .thenThrow(new GenreByIdNotFoundException(id));

        mockMvc.perform(
                get(UrlUtils.builder(BASE_PATH).add(id).build())
        ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Жанр с идентификатором: \"%s\" не найден".formatted(id)));
    }
}
