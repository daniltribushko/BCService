package ru.tdd.author.integrations.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.author.TestcontainersConfiguration;
import ru.tdd.author.application.dto.authors.CreateAuthorDTO;
import ru.tdd.author.application.dto.authors.UpdateAuthorDTO;
import ru.tdd.author.application.utils.JsonUtils;
import ru.tdd.author.database.entitites.Author;
import ru.tdd.author.database.entitites.Country;
import ru.tdd.author.database.repositories.AuthorRepository;
import ru.tdd.author.database.repositories.CountryRepository;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 * Набор тестов для контроллера автоа
 */
@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@DisplayName("Контроллер авторов")
public class AuthorControllerTest {

    private static final String BASE_URL = "/authors";

    private final MockMvc mockMvc;

    private final CountryRepository countryRepository;

    private final AuthorRepository authorRepository;

    @Autowired
    AuthorControllerTest(
            MockMvc mockMvc,
            CountryRepository countryRepository,
            AuthorRepository authorRepository
    ) {
        this.mockMvc = mockMvc;
        this.countryRepository = countryRepository;
        this.authorRepository = authorRepository;
    }

    @BeforeEach
    void cleanDb() {
        authorRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    @DisplayName("Удачное сохранение")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void successTest() throws Exception {
        Country country = new Country("Россия");

        country.setId(UUID.randomUUID());

        countryRepository.save(country);

        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                JsonUtils.toJson(
                                        new CreateAuthorDTO(
                                                "Иванов",
                                                null,
                                                "Иван",
                                                country.getId()
                                        )
                                )
                        )
        );

        response.andExpect(status().isCreated());
        Assertions.assertEquals(1, authorRepository.count());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Неудачное сохранение - страна по идентификатору не найдена")
    void saveCountryNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                JsonUtils.toJson(
                                        new CreateAuthorDTO(
                                                "Иванов",
                                                null,
                                                "Иван",
                                                UUID.randomUUID()
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Неудачное сохранение - дто не валидный")
    void saveCountryDtoNotValidFailTest() throws Exception {
        ResultActions response1 = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                JsonUtils.toJson(
                                        new CreateAuthorDTO(
                                                "",
                                                null,
                                                "",
                                                null
                                        )
                                )
                        )
        );

        ResultActions response2 = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                JsonUtils.toJson(
                                        new CreateAuthorDTO(
                                                "",
                                                null,
                                                "Иван",
                                                UUID.randomUUID()
                                        )
                                )
                        )
        );

        ResultActions response3 = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                JsonUtils.toJson(
                                        new CreateAuthorDTO(
                                                "Иванов",
                                                null,
                                                null,
                                                UUID.randomUUID()
                                        )
                                )
                        )
        );

        response1.andExpect(status().isUnprocessableEntity());
        response2.andExpect(status().isUnprocessableEntity());
        response3.andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser("USER")
    @DisplayName("Неудачное сохранение - пользователь не является администатором")
    void saveUserNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                JsonUtils.toJson(
                                        new CreateAuthorDTO(
                                                "Иванов",
                                                null,
                                                "Иван",
                                                UUID.randomUUID()
                                        )
                                )
                        )
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Удачное обновление")
    void updateSuccessTest() throws Exception {
        Country country1 = new Country("Россия");
        Country country2 = new Country("Китай");

        country1.setId(UUID.randomUUID());
        country2.setId(UUID.randomUUID());

        countryRepository.saveAll(List.of(country1, country2));

        Author author = new Author(
                "Иванов",
                "Иванович",
                "Иван",
                country1.getId()
        );

        authorRepository.save(author);

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                JsonUtils.toJson(
                                        new UpdateAuthorDTO(
                                                "Новый автор",
                                                null,
                                                "Автор",
                                                country2.getId()
                                        )
                                )
                        )

        );

        response.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.id",
                                is(author.getId().toString())
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.lastName",
                                is("Новый автор")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.middleName",
                                nullValue()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.firstName",
                                is("Автор")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.country.id",
                                is(country2.getId().toString())
                        )
                );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Неудачное обновление - автор по идентификатору не найден")
    void updateAuthorNotFoundFailTest() throws Exception {
        UUID authorId = UUID.randomUUID();

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + authorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                JsonUtils.toJson(
                                        new UpdateAuthorDTO()
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Автор с идентификатором: " + authorId + " не найден")
                        )
                );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Неудачное обновление - страна по идентификатору не найден")
    void updateCountryNotFoundFailTest() throws Exception {
        UUID countryId = UUID.randomUUID();

        Author author = new Author(
                "Тест",
                null,
                "Тест",
                UUID.randomUUID()
        );

        authorRepository.save(author);

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                JsonUtils.toJson(
                                        new UpdateAuthorDTO(
                                                null,
                                                null,
                                                null,
                                                countryId
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Страна с идентификатором: " + countryId + " не найдена")
                        )
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Неудачное обновление - пользователь не является администратором")
    void updateUserNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                JsonUtils.toJson(
                                        new UpdateAuthorDTO()
                                )
                        )
        );

        response.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("Удачное получение по идентификатору")
    void getByIdSuccessTest() throws Exception {
        Country country = new Country("Россия");

        country.setId(UUID.randomUUID());

        countryRepository.save(country);

        Author author1 = new Author(
                "1",
                null,
                "1",
                country.getId()
        );

        Author author2 = new Author(
                "2",
                null,
                "2",
                country.getId()
        );

        authorRepository.saveAll(List.of(author1, author2));

        ResultActions response = mockMvc.perform(
                get(BASE_URL + "/" + author2.getId())
        );

        response.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.id",
                                is(author2.getId().toString())
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.lastName",
                                is("2")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.middleName",
                                nullValue()
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.firstName",
                                is("2")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.country.id",
                                is(country.getId().toString())
                        )
                );
    }

    @Test
    @WithMockUser
    @DisplayName("Неудачное получение по идентификатору - автор по идентификатору не найден")
    void getByIdNotFoundFailTest() throws Exception {
        UUID authorId = UUID.randomUUID();

        ResultActions response = mockMvc.perform(
                get(BASE_URL + "/" + authorId)
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Автор с идентификатором: " + authorId + " не найден")
                        )
                );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Удачное удаление")
    void deleteSuccess() throws Exception {
        Country country = new Country("Россия");
        country.setId(UUID.randomUUID());

        countryRepository.save(country);

        Author author1 = new Author(
                "1",
                null,
                "1",
                country.getId()
        );

        Author author2 = new Author(
                "2",
                null,
                "2",
                country.getId()
        );

        authorRepository.saveAll(List.of(author1, author2));

        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + author2.getId())
        );

        response.andExpect(status().isNoContent());
        Assertions.assertEquals(1, authorRepository.count());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Неудачное удаление - автор по идентификатору не найден")
    void deleteNotFoundFailTest() throws Exception {
        UUID authorId = UUID.randomUUID();

        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + authorId)
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Автор с идентификатором: " + authorId + " не найден")
                        )
                );
    }

    @Test
    @DisplayName("Неудачное удаление - пользователь не является администратором")
    void deleteUserNotAdminFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + UUID.randomUUID())
        );

        response.andExpect(status().isForbidden());
    }
}
