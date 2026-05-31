package ru.tdd.core.application.services;

import ru.tdd.core.controller.dto.users.UserDto;

import javax.crypto.SecretKey;

/**
 * @author Tribushko Danil
 * @since 04.05.2026
 * Сервис для работы с jwt токенами
 */
public interface JwtService {

    UserDto parseToken(String token, String secretKey);

    boolean validateToken(String token, String secretKey);

    SecretKey getSecret(String secretKey);

    String generateToken(UserDto user, String secretKey);
}
