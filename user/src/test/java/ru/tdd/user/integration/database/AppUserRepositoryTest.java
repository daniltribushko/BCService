package ru.tdd.user.integration.database;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.tdd.user.TestcontainersConfiguration;
import ru.tdd.user.application.models.enums.Role;
import ru.tdd.user.database.entities.user.AppUser;
import ru.tdd.user.database.repositories.AppUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 31.01.2026
 * Тесты для репозитория по работе с пользователями приложения
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportTestcontainers(value = TestcontainersConfiguration.class)
public class AppUserRepositoryTest {

    private AppUserRepository appUserRepository;

    @Autowired
    public AppUserRepositoryTest(
            AppUserRepository appUserRepository
    ) {
        this.appUserRepository = appUserRepository;
    }

    @BeforeEach
    void cleanDb() {
        appUserRepository.deleteAll();
    }

    @Test
    void saveTest() {
        AppUser user = AppUser.appUserBuilder()
                .email("test_email")
                .chatId(1L)
                .username("user")
                .password("123")
                .roles(List.of(Role.ADMIN))
                .build();

        long expectedCount = appUserRepository.count() + 1;

        appUserRepository.save(user);

        long actualCount = appUserRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void updateTest() {
        AppUser user = AppUser.appUserBuilder()
                .email("test_email")
                .chatId(1L)
                .username("admin")
                .password("12345678")
                .roles(List.of(Role.ADMIN))
                .build();

        user = appUserRepository.save(user);

        user.setEmail("new_email@bk.ru");
        user.setChatId(2L);

        appUserRepository.save(user);

        Optional<AppUser> optUser = appUserRepository.findById(user.getId());

        Assertions.assertTrue(optUser.isPresent());

        AppUser updatedUser = optUser.get();

        Assertions.assertEquals("new_email@bk.ru", updatedUser.getEmail());
        Assertions.assertEquals(2L, updatedUser.getChatId());
    }

    @Test
    void deleteTest() {
        AppUser user = AppUser.appUserBuilder()
                .email("test_email")
                .chatId(1L)
                .username("admin")
                .password("12345678")
                .roles(List.of(Role.ADMIN))
                .build();

        appUserRepository.save(user);

        long expectedCount = appUserRepository.count() - 1;

        appUserRepository.delete(user);

        long actualCount = appUserRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void findByIdTest() {
        AppUser user1 = AppUser.appUserBuilder()
                .email("test_email")
                .chatId(1L)
                .username("admin")
                .password("12345678")
                .roles(List.of(Role.ADMIN))
                .build();

        AppUser user2 = AppUser.appUserBuilder()
                .email("email")
                .chatId(2L)
                .username("user")
                .password("12345678")
                .roles(List.of(Role.USER))
                .build();

        appUserRepository.saveAll(List.of(user1, user2));

        Optional<AppUser> userOpt1 = appUserRepository.findById(user2.getId());
        Optional<AppUser> userOpt2 = appUserRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(userOpt1.isPresent());
        Assertions.assertTrue(userOpt2.isEmpty());
        Assertions.assertEquals(user2.getId(), userOpt1.get().getId());
    }
}
