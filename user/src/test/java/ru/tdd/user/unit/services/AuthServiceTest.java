package ru.tdd.user.unit.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tdd.user.application.models.dto.JwtTokenDTO;
import ru.tdd.user.application.models.dto.SignIn;
import ru.tdd.user.application.models.dto.SignUp;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.application.models.exceptions.user.UserByChatIdAlreadyExistsException;
import ru.tdd.user.application.models.exceptions.user.UserByEmailAlreadyExistsException;
import ru.tdd.user.application.models.exceptions.user.UserByUsernameAlreadyExistsException;
import ru.tdd.user.application.services.JwtTokenService;
import ru.tdd.user.application.services.imp.AuthServiceImp;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.entities.user.SystemUser;
import ru.tdd.user.database.repositories.AppUserRepository;
import ru.tdd.user.database.repositories.SystemUserRepository;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author Tribushko Danil
 * @since 05.02.2026
 * Набор unit тестов для сервиса авторизации и регистрации пользователей
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private SystemUserRepository systemUserRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImp authService;

    @Test
    void signUpSuccessTest() {
        SignUp signUp = new SignUp(
                "user",
                "test_email",
                1L,
                "123"
        );

        Date date = new Date();

        Mockito.when(systemUserRepository.existsByUsername(signUp.getUsername())).thenReturn(false);
        Mockito.when(appUserRepository.existsByChatId(signUp.getChatId())).thenReturn(false);
        Mockito.when(appUserRepository.existsByChatId(signUp.getChatId())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(signUp.getPassword())).thenReturn("encoded_password");
        Mockito.when(jwtTokenService.generate(any(AppUser.class))).thenReturn(
                new JwtTokenDTO(
                        "generated_token",
                        date.getTime()
                )
        );
        JwtTokenDTO actual = authService.signUp(signUp);

        Assertions.assertEquals("generated_token", actual.getToken());
    }

    @Test
    void signUpAlreadyExistsByUsernameFailTest() {
        SignUp signUp = new SignUp(
                "user",
                null,
                null,
                null
        );

        Mockito.when(systemUserRepository.existsByUsername(signUp.getUsername())).thenReturn(true);

        UserByUsernameAlreadyExistsException actual = Assertions.assertThrows(
                UserByUsernameAlreadyExistsException.class,
                () -> authService.signUp(signUp)
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользователь с указанным именем уже создан", actual.getMessage());
    }

    @Test
    void signUpAlreadyExistsByEmailFailTest() {
        SignUp signUp = new SignUp(
                "user",
                "test_email",
                null,
                null
        );

        Mockito.when(systemUserRepository.existsByUsername(signUp.getUsername())).thenReturn(false);
        Mockito.when(appUserRepository.existsByEmail(signUp.getEmail())).thenReturn(true);

        UserByEmailAlreadyExistsException actual = Assertions.assertThrows(
                UserByEmailAlreadyExistsException.class,
                () -> authService.signUp(signUp)
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользователь с указанным электронным адресом уже создан", actual.getMessage());
    }

    @Test
    void signUpAlreadyExistsByChatIdFailTest() {
        SignUp signUp = new SignUp(
                "user",
                null,
                1L,
                null
        );

        Mockito.when(systemUserRepository.existsByUsername(signUp.getUsername())).thenReturn(false);
        Mockito.when(appUserRepository.existsByChatId(signUp.getChatId())).thenReturn(true);

        UserByChatIdAlreadyExistsException actual = Assertions.assertThrows(
                UserByChatIdAlreadyExistsException.class,
                () -> authService.signUp(signUp)
        );

        Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользаватель с указанным идентификатором телеграма уже найден", actual.getMessage());
    }

    @Test
    void signInByAppUserSuccessTest() {
        AppUser user = AppUser.appUserBuilder()
                .username("user")
                .password("123")
                .build();

        Mockito.when(userDetailsService.loadUserByUsername("user")).thenReturn(user);
        Mockito.when(jwtTokenService.generate(user)).thenReturn(
                new JwtTokenDTO(
                        "app_user_token",
                        1L
                )
        );

        JwtTokenDTO actual = authService.signIn(new SignIn("user", "123"));

        Assertions.assertEquals("app_user_token", actual.getToken());
    }

    @Test
    void signInBySystemUserSuccessTest() {
        SystemUser user = SystemUser.builder()
                .username("admin")
                .password("1234")
                .roles(List.of(Role.ADMIN))
                .build();

        Mockito.when(userDetailsService.loadUserByUsername("admin")).thenReturn(user);
        Mockito.when(jwtTokenService.generate(user)).thenReturn(
                new JwtTokenDTO(
                        "system_user_token",
                        2L
                )
        );

        JwtTokenDTO actual = authService.signIn(new SignIn("admin", "1234"));

        Assertions.assertEquals("system_user_token", actual.getToken());
    }

    @Test
    void signInUserByUsernameNotFoundFailTest() {
        Mockito.when(userDetailsService.loadUserByUsername("test_user"))
                .thenThrow(new UsernameNotFoundException("Пользователь test_user не найден"));

        UsernameNotFoundException actual = Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> authService.signIn(new SignIn("test_user", "123"))
        );

        Assertions.assertEquals("Пользователь test_user не найден", actual.getMessage());
    }
}
