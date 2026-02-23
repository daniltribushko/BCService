package ru.tdd.author.application.services;

import ru.tdd.author.application.dto.UserDTO;

/**
 * @author Tribushko Danil
 * @since 22.02.2026
 * Сервис для работы с jwt токеном
 */
public interface JwtService {

    UserDTO parse(String token);
}
