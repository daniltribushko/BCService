package ru.tdd.user.integration.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.user.TestcontainersConfiguration;
import ru.tdd.user.application.models.dto.JwtTokenDTO;
import ru.tdd.user.application.models.dto.SignIn;
import ru.tdd.user.application.models.dto.SignUp;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.application.models.exceptions.user.UserByChatIdAlreadyExistsException;
import ru.tdd.user.application.models.exceptions.user.UserByEmailAlreadyExistsException;
import ru.tdd.user.application.models.exceptions.user.UserByUsernameAlreadyExistsException;
import ru.tdd.user.application.services.AuthService;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.repositories.AppUserRepository;
import ru.tdd.user.database.repositories.SystemUserRepository;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 05.02.2026
 * Набор интеграционных тестов сервиса для авторизации и регистрации пользователей
 */
@Testcontainers
@SpringBootTest
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthServiceTest {

    private final SystemUserRepository systemUserRepository;

    private final AppUserRepository appUserRepository;

    private final AuthService authService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceTest(
            SystemUserRepository systemUserRepository,
            AppUserRepository appUserRepository,
            AuthService authService,
            PasswordEncoder passwordEncoder
    ) {
        this.systemUserRepository = systemUserRepository;
        this.appUserRepository = appUserRepository;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void cleanDb() {
        systemUserRepository.deleteAll();
    }

    @Test
    void signUpSuccessTest() {
        SignUp dto = new SignUp(
                "user",
                "test_email",
                1L,
                "123"
        );

        long expectedCount = appUserRepository.count() + 1;

        JwtTokenDTO actual = authService.signUp(dto);

        long actualCount = appUserRepository.count();

        Assertions.assertNotNull(actual.getToken());
        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void signUpUserByUsernameAlreadyExistsFailTest() {
        AppUser user = AppUser.appUserBuilder()
                .username("user")
                .chatId(15L)
                .password("1234")
                .roles(List.of(Role.USER))
                .build();

        appUserRepository.save(user);

        UserByUsernameAlreadyExistsException actual = Assertions.assertThrows(
                UserByUsernameAlreadyExistsException.class,
                () -> authService.signUp(new SignUp("user", null, null, "1234"))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользователь с указанным именем уже создан", actual.getMessage());
    }

    @Test
    void signUpUserByEmailAlreadyExistsFailTest() {
        AppUser user = AppUser.appUserBuilder()
                .username("user")
                .email("user_email")
                .password("1234")
                .roles(List.of(Role.USER))
                .build();

        appUserRepository.save(user);

        UserByEmailAlreadyExistsException actual = Assertions.assertThrows(
                UserByEmailAlreadyExistsException.class,
                () -> authService.signUp(new SignUp("admin", "user_email", null, "1234"))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользователь с указанным электронным адресом уже создан", actual.getMessage());
    }

    @Test
    void signUpUserByChatIdAlreadyExistsFailTest() {
        AppUser user = AppUser.appUserBuilder()
                .username("user")
                .chatId(5L)
                .password("1234")
                .roles(List.of(Role.USER))
                .build();

        appUserRepository.save(user);

        UserByChatIdAlreadyExistsException actual = Assertions.assertThrows(
                UserByChatIdAlreadyExistsException.class,
                () -> authService.signUp(new SignUp("admin", null, 5L, "1234"))
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользаватель с указанным идентификатором телеграма уже найден", actual.getMessage());
    }

    @Test
    void signInSuccessTest() {
        AppUser user = AppUser.appUserBuilder()
                .username("user")
                .password(passwordEncoder.encode("1234"))
                .roles(List.of(Role.USER))
                .build();

        appUserRepository.save(user);

        JwtTokenDTO actual = authService.signIn(new SignIn("user", "1234"));

        Assertions.assertNotNull(actual.getToken());
        Assertions.assertTrue(actual.getExpiration() > 0);
    }

    @Test
    void signInUserNotFoundTest() {
        UsernameNotFoundException actual = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> authService.signIn(new SignIn("user", "1234"))
        );

        Assertions.assertEquals("Пользователь user не найден", actual.getMessage());
    }
}
