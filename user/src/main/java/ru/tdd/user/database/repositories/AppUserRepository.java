package ru.tdd.user.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.tdd.user.database.entities.user.AppUser;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 31.01.2026
 * Репозиторий для работы с пользователями приложения
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID>,
        JpaSpecificationExecutor<AppUser> {

    boolean existsByEmail(String email);

    boolean existsByChatId(Long chatId);

    Optional<AppUser> findByUsername(String username);
}
