package ru.tdd.geo.application.services;

import io.jsonwebtoken.Claims;

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

    /**
     * Получение данных из токена
     */
    Claims parseToken(String token);
}
