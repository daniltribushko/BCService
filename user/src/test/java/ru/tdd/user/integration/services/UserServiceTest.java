package ru.tdd.user.integration.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.user.TestcontainersConfiguration;
import ru.tdd.user.application.models.dto.UpdateUserDTO;
import ru.tdd.user.application.models.dto.UserDTO;
import ru.tdd.user.application.models.dto.UserDetailsDTO;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.application.models.exceptions.AuthenticationException;
import ru.tdd.user.application.models.exceptions.user.UserByIdNotFoundException;
import ru.tdd.user.application.models.exceptions.user.UserByUsernameAlreadyExistsException;
import ru.tdd.user.application.services.UserService;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.entities.user.SystemUser;
import ru.tdd.user.database.repositories.AppUserRepository;
import ru.tdd.user.database.repositories.SystemUserRepository;
import ru.tdd.user.utils.SecurityContextUtils;

import java.util.List;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 15.02.2026
 * Набор интеграционных тестов для сервиса пол работе с пользователями
 */
@Testcontainers
@SpringBootTest
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceTest {

    private final AppUserRepository appUserRepository;

    private final SystemUserRepository systemUserRepository;

    private final UserService userService;

    @Autowired
    public UserServiceTest(
            AppUserRepository appUserRepository,
            SystemUserRepository systemUserRepository,
            UserService userService
    ) {
        this.appUserRepository = appUserRepository;
        this.systemUserRepository = systemUserRepository;
        this.userService = userService;
    }

    @BeforeEach
    void cleanDb() {
        appUserRepository.deleteAll();
        systemUserRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user", password = "123")
    void updateSuccess() {
        SystemUser user = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.save(user);

        UserDTO actual = userService.update(
                user.getId(),
                new UpdateUserDTO(
                        "new_name",
                        "new_email@gmail.com",
                        null
                )
        );

        Assertions.assertEquals(user.getId(), actual.getId());
        Assertions.assertEquals("new_name", actual.getUsername());
        Assertions.assertEquals("new_email@gmail.com", actual.getEmail());
        Assertions.assertEquals("123", user.getPassword());
    }

    @Test
    void updateIsNotCurrentUserOwnerFailTest() {
        SystemUser user = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.save(user);

        SecurityContextUtils.setUserInContext(user);

        AuthenticationException actual = Assertions.assertThrows(
                AuthenticationException.class,
                () -> userService.update(UUID.randomUUID(), new UpdateUserDTO())
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользователь пытается изменить другого пользователя", actual.getMessage());
    }

    @Test
    void updateUserByUsernameAlreadyExistsFailTest() {
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

        SecurityContextUtils.setUserInContext(user2);

        UserByUsernameAlreadyExistsException actual = Assertions.assertThrows(
                UserByUsernameAlreadyExistsException.class,
                () -> userService.update(user2.getId(), new UpdateUserDTO("user", null, null))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользователь с указанным именем уже создан", actual.getMessage());
    }

    @Test
    @WithMockUser(username = "user", password = "123")
    void updateUserNotFoundFailTest() {
        UserByIdNotFoundException actual = Assertions.assertThrows(
                UserByIdNotFoundException.class,
                () -> userService.update(UUID.randomUUID(), new UpdateUserDTO())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
    }

    @Test
    void deleteSuccessTest() {
        SystemUser user = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.save(user);

        SecurityContextUtils.setUserInContext(user);

        long expectedCount = systemUserRepository.count() - 1;
        userService.delete(user.getId());
        long actualCount = systemUserRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void deleteIsNotCurrentUserOwnerFailTest() {
        SystemUser user = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.save(user);

        SecurityContextUtils.setUserInContext(user);

        AuthenticationException actual = Assertions.assertThrows(
                AuthenticationException.class,
                () -> userService.delete(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользователь пытается изменить другого пользователя", actual.getMessage());
    }

    @Test
    @WithMockUser(username = "user")
    void deleteUserNotFoundFailTest() {
        UserByIdNotFoundException actual = Assertions.assertThrows(
                UserByIdNotFoundException.class,
                () -> userService.delete(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользователь по указанному идентификатору не найден", actual.getMessage());
    }

    @Test
    void getByIdSuccessTest() {
        SystemUser user1 = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .email("test_email@gmail.com")
                .chatId(1L)
                .roles(List.of(Role.USER))
                .build();

        SystemUser user2 = AppUser.appUserBuilder()
                .username("admin")
                .password("123")
                .chatId(100L)
                .roles(List.of(Role.USER))
                .build();

        SystemUser user3 = AppUser.builder()
                .username("system")
                .password("1234")
                .roles(List.of(Role.USER))
                .build();

        systemUserRepository.saveAll(List.of(user1, user2, user3));

        UserDetailsDTO actual = userService.getById(user2.getId());

        Assertions.assertEquals(user2.getId(), actual.getId());
        Assertions.assertEquals("admin", actual.getUsername());
        Assertions.assertEquals(100L, actual.getChatId());
    }

    @Test
    void getByIdUSerNotFoundFailTest() {
        UserByIdNotFoundException actual = Assertions.assertThrows(
                UserByIdNotFoundException.class,
                () -> userService.getById(UUID.randomUUID())
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользователь по указанному идентификатору не найден", actual.getMessage());
    }
}
