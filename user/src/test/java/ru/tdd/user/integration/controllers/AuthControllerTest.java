package ru.tdd.user.integration.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.user.TestcontainersConfiguration;
import ru.tdd.user.application.models.dto.DTOMapper;
import ru.tdd.user.application.models.dto.SignIn;
import ru.tdd.user.application.models.dto.SignUp;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.repositories.AppUserRepository;
import ru.tdd.user.database.repositories.SystemUserRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 08.02.2026
 * Набор тестов для контроллера авторизации и регистрации пользователей
 */
@SpringBootTest
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    private static final String SIGN_UP_URL = "/auth/sign-up";

    private static final String SIGN_IN_URL = "/auth/sign-in";

    private final SystemUserRepository systemUserRepository;

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final MockMvc mockMvc;

    @Autowired
    public AuthControllerTest(
            SystemUserRepository systemUserRepository,
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            MockMvc mockMvc
    ) {
        this.systemUserRepository = systemUserRepository;
        this.appUserRepository = appUserRepository;
        this.mockMvc = mockMvc;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void cleanDb() {
        systemUserRepository.deleteAll();
    }

    @Test
    void signUpSuccessTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new SignUp(
                                                "user",
                                                "test_email",
                                                123L,
                                                "123"
                                        )
                                )
                        )
        );

        response.andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.token",
                                notNullValue(String.class)
                        )
                );

        Optional<AppUser> userOpt = appUserRepository.findByUsername("user");

        Assertions.assertTrue(userOpt.isPresent());
    }

    @Test
    void signUpUserByUsernameAlreadyExistsFailTest() throws Exception {
        AppUser user = AppUser.appUserBuilder()
                .username("already_exists_user")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        appUserRepository.save(user);

        ResultActions response = mockMvc.perform(
                post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new SignUp(
                                            "already_exists_user",
                                                null,
                                                null,
                                                "123"
                                        )
                                )
                        )
        );

        response.andExpect(status().isConflict())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Пользователь с указанным именем уже создан")
                        )
                );
    }

    @Test
    void signUpUserByEmailAlreadyExistsFailTest() throws Exception {
        AppUser user = AppUser.appUserBuilder()
                .username("admin")
                .email("already_exists_user")
                .password("123")
                .roles(List.of(Role.USER, Role.ADMIN))
                .build();

        appUserRepository.save(user);

        ResultActions response = mockMvc.perform(
                post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new SignUp(
                                                "user",
                                                "already_exists_user",
                                                null,
                                                "123"
                                        )
                                )
                        )
        );

        response.andExpect(status().isConflict())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Пользователь с указанным электронным адресом уже создан")
                        )
                );
    }

    @Test
    void signUpUserByChatIdAlreadyExistsFailTest() throws Exception {
        AppUser user = AppUser.appUserBuilder()
                .username("admin")
                .chatId(1L)
                .password("123")
                .roles(List.of(Role.USER, Role.ADMIN))
                .build();

        appUserRepository.save(user);

        ResultActions response = mockMvc.perform(
                post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new SignUp(
                                                "user",
                                                null,
                                                1L,
                                                "123"
                                        )
                                )
                        )
        );

        response.andExpect(status().isConflict())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Пользаватель с указанным идентификатором телеграма уже найден")
                        )
                );
    }

    @Test
    void signInSuccessTest() throws Exception {
        AppUser user = AppUser.appUserBuilder()
                .username("user")
                .password(passwordEncoder.encode("12345678"))
                .roles(List.of(Role.USER, Role.ADMIN))
                .build();

        appUserRepository.save(user);

        ResultActions response = mockMvc.perform(
                post(SIGN_IN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new SignIn(
                                              "user",
                                              "12345678"
                                        )
                                )
                        )
        );

        response.andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.token",  notNullValue())
                );
    }

    @Test
    void signInUserNorFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(
                post(SIGN_IN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                DTOMapper.toJson(
                                        new SignIn(
                                                "user",
                                                "12345678"
                                        )
                                )
                        )
        );

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message",  is("Пользователь user не найден"))
                );
    }
}
