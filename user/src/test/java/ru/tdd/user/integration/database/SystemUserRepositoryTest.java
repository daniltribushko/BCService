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
import ru.tdd.user.database.entities.user.SystemUser;
import ru.tdd.user.database.repositories.SystemUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 31.01.2026
 * Набор тестов для репозитория системных пользователей
 */
@DataJpaTest
@Testcontainers
@ImportTestcontainers(value = TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SystemUserRepositoryTest {

    @Autowired
    private SystemUserRepository systemUserRepository;

    @BeforeEach
    void cleanDb() {
        systemUserRepository.deleteAll();
    }

    @Test
    void saveTest() {
        SystemUser user1 = SystemUser.builder()
                .roles(List.of(Role.ADMIN))
                .lastDateOnline(LocalDateTime.now())
                .password("123")
                .username("admin")
                .build();

        SystemUser user2 = SystemUser.builder()
                .roles(List.of(Role.USER))
                .lastDateOnline(LocalDateTime.now())
                .username("user")
                .password("123")
                .build();

        long expectedCount = systemUserRepository.count() + 2;

        systemUserRepository.saveAll(List.of(user1, user2));

        long actualCount = systemUserRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void updateTest() {
        SystemUser user = SystemUser.builder()
                .roles(List.of(Role.ADMIN))
                .lastDateOnline(LocalDateTime.now())
                .password("123")
                .username("admin")
                .build();

        user = systemUserRepository.save(user);

        user.setUsername("New Username");

        SystemUser updatedUser = systemUserRepository.save(user);

        Assertions.assertEquals(user.getId(), updatedUser.getId());
        Assertions.assertEquals("New Username", updatedUser.getUsername());
    }

    @Test
    void findByIdTest() {
        SystemUser user = SystemUser.builder()
                .roles(List.of(Role.ADMIN))
                .lastDateOnline(LocalDateTime.now())
                .password("123")
                .username("user")
                .build();

        systemUserRepository.save(user);

        Optional<SystemUser> userOpt1 = systemUserRepository.findById(user.getId());
        Optional<SystemUser> userOpt2 = systemUserRepository.findById(UUID.randomUUID());

        Assertions.assertTrue(userOpt1.isPresent());
        Assertions.assertFalse(userOpt2.isPresent());
        Assertions.assertEquals(user.getId(), userOpt1.get().getId());
    }

    @Test
    void deleteTest() {
        SystemUser user = SystemUser.builder()
                .roles(List.of(Role.ADMIN))
                .lastDateOnline(LocalDateTime.now())
                .password("123")
                .username("user_delete")
                .build();

        systemUserRepository.save(user);

        Optional<SystemUser> userOpt = systemUserRepository.findById(user.getId());

        long expectedCount = systemUserRepository.count() - 1;
        systemUserRepository.delete(userOpt.get());
        long actualCount = systemUserRepository.count();

        Assertions.assertEquals(expectedCount, actualCount);
    }

    @Test
    void existsByUsername() {
        SystemUser user = SystemUser.builder()
                .roles(List.of(Role.ADMIN))
                .lastDateOnline(LocalDateTime.now())
                .password("123")
                .username("exists_user")
                .build();

        systemUserRepository.save(user);

        Assertions.assertTrue(systemUserRepository.existsByUsername("exists_user"));
        Assertions.assertFalse(systemUserRepository.existsByUsername("admin"));
    }
}
