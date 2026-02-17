package ru.tdd.user.integration.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.user.TestcontainersConfiguration;
import ru.tdd.user.application.models.dto.DTOMapper;
import ru.tdd.user.application.models.dto.GetUserListParametersDTO;
import ru.tdd.user.application.models.dto.UpdateUserDTO;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.entities.user.SystemUser;
import ru.tdd.user.database.repositories.SystemUserRepository;
import ru.tdd.user.utils.SecurityContextUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Tribushko Danil
 * @since 17.02.2026
 *
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ImportTestcontainers(value = TestcontainersConfiguration.class)
class UserControllerTest {

    private static final String BASE_URL = "/users";

    private final MockMvc mockMvc;

    private final SystemUserRepository systemUserRepository;

    @Autowired
    public UserControllerTest(
            MockMvc mockMvc,
            SystemUserRepository systemUserRepository
    ) {
        this.mockMvc = mockMvc;
        this.systemUserRepository = systemUserRepository;
    }

    @BeforeEach
    void cleanDb() {
        systemUserRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "123")
    void updateSuccessTest() throws Exception {
        SystemUser user = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .roles(List.of(Role.USER))
                .email("test_email@gmail.com")
                .build();

        systemUserRepository.save(user);

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateUserDTO(
                                                "new_username",
                                                "new_email",
                                                "new_password"
                                        )
                                )
                        )
        );

        response.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.id",
                                is(user.getId().toString())
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.username",
                                is("new_username")
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.email",
                                is("new_email")
                        )
                );
    }

    @Test
    void updateIsNotCurrentUserOwnerFailTest() throws Exception {
        SystemUser user = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.save(user);

        SecurityContextUtils.setUserInContext(user);

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateUserDTO()
                                )
                        )
        );

        response.andExpect(status().isForbidden())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Пользователь пытается изменить другого пользователя")
                        )
                );
    }

    @Test
    void updateAlreadyExistsByUsernameFailTest() throws Exception {
        SystemUser user1 = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        SystemUser user2 = AppUser.appUserBuilder()
                .username("admin")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.saveAll(List.of(user1, user2));

        SecurityContextUtils.setUserInContext(user1);

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + user1.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateUserDTO(
                                                "admin",
                                                null,
                                                null
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
    @WithMockUser(username = "user")
    void updateAlreadyExistsByEmailFailTest() throws Exception {
        SystemUser user1 = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .email("email1")
                .roles(List.of(Role.USER))
                .build();

        SystemUser user2 = AppUser.appUserBuilder()
                .username("admin")
                .password("123")
                .email("test_email")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.saveAll(List.of(user1, user2));

        ResultActions response = mockMvc.perform(
                put(BASE_URL + "/" + user1.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                DTOMapper.toJson(
                                        new UpdateUserDTO(
                                                null,
                                                "test_email",
                                                null
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
    @WithMockUser(username = "user")
    void deleteSuccessTest() throws Exception {
        SystemUser user = SystemUser.builder()
                .username("user")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.save(user);

        long expectedCount = systemUserRepository.count() - 1;

        ResultActions response = mockMvc.perform(
                delete(BASE_URL + "/" + user.getId())
        );

        long actualCount = systemUserRepository.count();

        response.andExpect(status().isNoContent());
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void deleteIsNotCurrentUserOwnerFailTest() throws Exception {
        SystemUser user = SystemUser.builder()
                .username("user")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.save(user);

        SecurityContextUtils.setUserInContext(user);

        ResultActions response = mockMvc.perform(delete(BASE_URL + "/" + UUID.randomUUID()));

        response.andExpect(status().isForbidden())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Пользователь пытается изменить другого пользователя")
                        )
                );
    }

    @Test
    void findByIdSuccessTest() {
        SystemUser user1 = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .email("email1")
                .roles(List.of(Role.USER))
                .build();

        SystemUser user2 = AppUser.appUserBuilder()
                .username("admin")
                .password("123")
                .email("test_email")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.saveAll(List.of(user1, user2));
    }

    @Test
    void findByIdNotFoundFailTest() throws Exception {
        ResultActions response = mockMvc.perform(get(BASE_URL + "/" + UUID.randomUUID()));

        response.andExpect(status().isNotFound())
                .andExpect(
                        jsonPath(
                                "$.message",
                                is("Пользователь по указанному идентификатору не найден")
                        )
                );
    }

    @Test
    void findAllTest() throws Exception {
        SystemUser user1 = SystemUser.builder()
                .username("teSt_UsEr")
                .password("123")
                .roles(List.of(Role.USER))
                .lastDateOnline(LocalDateTime.of(2002, 11, 15, 1, 1, 1))
                .build();

        SystemUser user2 = SystemUser.builder()
                .username("tTEST")
                .password("123")
                .roles(List.of(Role.USER))
                .lastDateOnline(LocalDateTime.of(2012, 1, 2, 1, 1, 1))
                .build();

        SystemUser user3 = AppUser.appUserBuilder()
                .username("tEsTinG")
                .password("123")
                .email("TeSt")
                .roles(List.of(Role.ADMIN))
                .lastDateOnline(LocalDateTime.of(2005, 1, 2, 1, 1, 1))
                .build();

        SystemUser user4 = AppUser.appUserBuilder()
                .username("example_user")
                .password("123")
                .email("test_eMaIl")
                .lastDateOnline(LocalDateTime.of(2004, 1, 1, 1, 1, 1))
                .roles(List.of(Role.USER))
                .build();

        SystemUser user5 = AppUser.appUserBuilder()
                .username("user1")
                .password("123")
                .email("EMaIl")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.saveAll(List.of(user1, user2, user3, user4, user5));

        ResultActions response1 = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                DTOMapper.toJson(
                                        new GetUserListParametersDTO(
                                                "TEST",
                                                null,
                                                List.of(Role.USER),
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null
                                        )
                                )
                        )
        );

        ResultActions response2 = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                DTOMapper.toJson(
                                        new GetUserListParametersDTO(
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                LocalDateTime.of(2000, 1, 1, 1, 1, 1),
                                                LocalDateTime.of(2005, 3, 1, 1, 1, 1)
                                        )
                                )
                        )
        );

        ResultActions response3 = mockMvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                DTOMapper.toJson(
                                        new GetUserListParametersDTO(
                                                null,
                                                "EmA",
                                                List.of(Role.USER),
                                                null,
                                                null,
                                                null,
                                                null,
                                                null,
                                                null
                                        )
                                )
                        )
        );

        response1.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.data.length()",
                                is(2)
                        )
                );

        response2.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.data.length()",
                                is(3)
                        )
                );

        response3.andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.data.length()",
                                is(2)
                        )
                );
    }
}
