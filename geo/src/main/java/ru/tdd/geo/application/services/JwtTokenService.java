package ru.tdd.geo.application.services;

import io.jsonwebtoken.Claims;
import ru.tdd.geo.application.models.dto.UserDTO;

/**
 * @author Tribushko Danil
 * @since 05.01.2026
 * Сервисы для работы с jwt токенами
 */
public interface JwtTokenService {

    /**
     * Валидация токена
     */
    boolean validateToken(String token);

    UserDTO getUser(String token);

    /**
     * Получение данных из токена
     */
    Claims parseToken(String token);
}
