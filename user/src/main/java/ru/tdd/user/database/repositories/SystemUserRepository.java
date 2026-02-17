package ru.tdd.user.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.tdd.user.database.entities.user.SystemUser;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 29.01.2026
 * Репозиторий для работы с системными пользователями
 */
@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, UUID>,
        JpaSpecificationExecutor<SystemUser> {

    boolean existsByUsername(String username);

    Optional<SystemUser> findByUsername(String username);
}
