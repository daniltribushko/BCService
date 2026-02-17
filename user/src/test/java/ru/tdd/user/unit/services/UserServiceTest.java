package ru.tdd.user.unit.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.tdd.user.application.models.dto.UpdateUserDTO;
import ru.tdd.user.application.models.dto.UserDTO;
import ru.tdd.user.application.models.dto.UserDetailsDTO;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.application.models.exceptions.AuthenticationException;
import ru.tdd.user.application.models.exceptions.user.UserByEmailAlreadyExistsException;
import ru.tdd.user.application.models.exceptions.user.UserByIdNotFoundException;
import ru.tdd.user.application.models.exceptions.user.UserByUsernameAlreadyExistsException;
import ru.tdd.user.application.services.imp.UserServiceImp;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.entities.user.SystemUser;
import ru.tdd.user.database.repositories.AppUserRepository;
import ru.tdd.user.database.repositories.SystemUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 15.02.2026
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private SystemUserRepository systemUserRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImp userService;

    @Test
    void updateSuccessTest() {
        UUID userId = UUID.randomUUID();
        SystemUser user = AppUser.appUserBuilder()
                .id(userId)
                .username("admin")
                .password("123")
                .roles(List.of(Role.ADMIN))
                .build();

        try (MockedStatic<SecurityContextHolder> holder = Mockito.mockStatic(SecurityContextHolder.class)) {
            holder.when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);

            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getPrincipal()).thenReturn(user);
            Mockito.when(systemUserRepository.findById(userId)).thenReturn(Optional.of(user));
            Mockito.when(appUserRepository.existsByEmail("new_email")).thenReturn(false);

            UserDTO actual = userService.update(
                    userId,
                    new UpdateUserDTO(
                            null,
                            "new_email",
                            "password"
                    )
            );

            Assertions.assertEquals(userId, actual.getId());
            Assertions.assertEquals("new_email", actual.getEmail());
            Assertions.assertEquals("password", user.getPassword());
        }
    }

    @Test
    void updateIsNotCurrentUserOwnerFailTest() {
        UUID userId = UUID.randomUUID();
        SystemUser user = AppUser.appUserBuilder()
                .id(userId)
                .username("admin")
                .password("123")
                .roles(List.of(Role.ADMIN))
                .build();

        try (MockedStatic<SecurityContextHolder> holder = Mockito.mockStatic(SecurityContextHolder.class)) {
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getPrincipal()).thenReturn(user);

            AuthenticationException actual = Assertions.assertThrows(
                    AuthenticationException.class,
                    () -> userService.update(UUID.randomUUID(), new UpdateUserDTO())
            );

            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), actual.getStatusCode());
            Assertions.assertEquals(
                    "Пользователь пытается изменить другого пользователя",
                    actual.getMessage()
            );
        }
    }

    @Test
    void updateUserNotFoundFailTest() {
        UUID userId = UUID.randomUUID();
        SystemUser user = AppUser.appUserBuilder()
                .id(userId)
                .username("admin")
                .password("123")
                .roles(List.of(Role.ADMIN))
                .build();

        try (MockedStatic<SecurityContextHolder> holder = Mockito.mockStatic(SecurityContextHolder.class)) {
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getPrincipal()).thenReturn(user);
            Mockito.when(systemUserRepository.findById(userId)).thenReturn(Optional.empty());

            UserByIdNotFoundException actual = Assertions.assertThrows(
                    UserByIdNotFoundException.class,
                    () -> userService.update(userId, new UpdateUserDTO())
            );

            Assertions.assertEquals(
                    HttpStatus.NOT_FOUND.value(),
                    actual.getStatusCode()
            );

            Assertions.assertEquals(
                    "Пользователь по указанному идентификатору не найден",
                    actual.getMessage()
            );
        }
    }

    @Test
    void updateUserByUsernameAlreadyExistsFailTest() {
        UUID userId = UUID.randomUUID();
        SystemUser user = AppUser.appUserBuilder()
                .id(userId)
                .username("admin")
                .password("123")
                .roles(List.of(Role.ADMIN))
                .build();

        try (MockedStatic<SecurityContextHolder> holder = Mockito.mockStatic(SecurityContextHolder.class)) {
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getPrincipal()).thenReturn(user);
            Mockito.when(systemUserRepository.findById(userId)).thenReturn(Optional.of(user));
            Mockito.when(systemUserRepository.existsByUsername("user")).thenReturn(true);

            UserByUsernameAlreadyExistsException actual = Assertions.assertThrows(
                    UserByUsernameAlreadyExistsException.class,
                    () -> userService.update(userId, new UpdateUserDTO("user", null, null))
            );

            Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
            Assertions.assertEquals("Пользователь с указанным именем уже создан", actual.getMessage());
        }
    }

    @Test
    void updateUserByEmailAlreadyExistsFailTest() {
        UUID userId = UUID.randomUUID();
        SystemUser user = AppUser.appUserBuilder()
                .id(userId)
                .username("user")
                .email("test_email@gmail.com")
                .password("123")
                .roles(List.of(Role.USER))
                .build();

        try (MockedStatic<SecurityContextHolder> holder = Mockito.mockStatic(SecurityContextHolder.class)) {
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getPrincipal()).thenReturn(user);
            Mockito.when(systemUserRepository.findById(userId)).thenReturn(Optional.of(user));
            Mockito.when(appUserRepository.existsByEmail("test_email@gmail.com")).thenReturn(true);

            UserByEmailAlreadyExistsException actual = Assertions.assertThrows(
                    UserByEmailAlreadyExistsException.class,
                    () -> userService.update(userId, new UpdateUserDTO(null, "test_email@gmail.com", null))
            );

            Assertions.assertEquals(HttpStatus.CONFLICT.value(), actual.getStatusCode());
            Assertions.assertEquals("Пользователь с указанным электронным адресом уже создан", actual.getMessage());
        }
    }

    @Test
    void deleteSuccessTest() {
        UUID userId = UUID.randomUUID();
        SystemUser user = AppUser.appUserBuilder()
                .id(userId)
                .username("user")
                .password("123")
                .roles(List.of(Role.ADMIN))
                .build();

        try (MockedStatic<SecurityContextHolder> holder = Mockito.mockStatic(SecurityContextHolder.class)) {
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getPrincipal()).thenReturn(user);
            Mockito.when(systemUserRepository.findById(userId)).thenReturn(Optional.of(user));

            userService.delete(userId);

            Mockito.verify(systemUserRepository).delete(user);
        }
    }

    @Test
    void deleteIsNotCurrentUserOwnerFailTest() {
        UUID userId = UUID.randomUUID();
        SystemUser user = AppUser.appUserBuilder()
                .id(userId)
                .username("user")
                .password("123")
                .roles(List.of(Role.ADMIN))
                .build();

        try (MockedStatic<SecurityContextHolder> holder = Mockito.mockStatic(SecurityContextHolder.class)) {
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getPrincipal()).thenReturn(user);

            AuthenticationException actual = Assertions.assertThrows(
                    AuthenticationException.class,
                    () -> userService.delete(UUID.randomUUID())
            );

            Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), actual.getStatusCode());
            Assertions.assertEquals("Пользователь пытается изменить другого пользователя", actual.getMessage());
        }
    }

    @Test
    void deleteUserNotFoundFailTest() {
        UUID userId = UUID.randomUUID();
        SystemUser user = AppUser.appUserBuilder()
                .id(userId)
                .username("user")
                .password("123")
                .roles(List.of(Role.ADMIN))
                .build();

        try (MockedStatic<SecurityContextHolder> holder = Mockito.mockStatic(SecurityContextHolder.class)) {
            holder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            Mockito.when(authentication.getPrincipal()).thenReturn(user);
            Mockito.when(systemUserRepository.findById(userId)).thenReturn(Optional.empty());

            UserByIdNotFoundException actual = Assertions.assertThrows(
                    UserByIdNotFoundException.class,
                    () -> userService.delete(userId)
            );

            Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
            Assertions.assertEquals("Пользователь по указанному идентификатору не найден", actual.getMessage());
        }
    }

    @Test
    void getByIdSuccessTest() {
        UUID userId = UUID.randomUUID();
        SystemUser user = AppUser.appUserBuilder()
                .id(userId)
                .username("user")
                .password("123")
                .roles(List.of(Role.ADMIN))
                .build();

        Mockito.when(systemUserRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDetailsDTO actual = userService.getById(userId);

        Assertions.assertEquals(userId, actual.getId());
        Assertions.assertEquals("user", actual.getUsername());
    }

    @Test
    void getByIdUserNotFoundFailTest() {
        UUID userId = UUID.randomUUID();
        Mockito.when(systemUserRepository.findById(userId)).thenReturn(Optional.empty());

        UserByIdNotFoundException actual = Assertions.assertThrows(
                UserByIdNotFoundException.class,
                () -> userService.getById(userId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), actual.getStatusCode());
        Assertions.assertEquals("Пользователь по указанному идентификатору не найден", actual.getMessage());
    }
}
