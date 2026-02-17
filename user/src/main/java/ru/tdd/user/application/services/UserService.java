package ru.tdd.user.application.services;

import org.springframework.transaction.annotation.Transactional;
import ru.tdd.user.application.models.dto.*;

import java.util.UUID;

/**
 * @author Tribushko Danil
 * @since 12.02.2026
 * Сервис для работы с пользователями
 */
public interface UserService {

    @Transactional
    UserDTO update(UUID id, UpdateUserDTO dto);

    @Transactional
    UserDetailsDTO getById(UUID id);

    @Transactional
    void delete(UUID id);

    @Transactional
    UserListDTO getAll(
           GetUserListParametersDTO dto,
           int page,
           int perPage
    );
}
