package ru.tdd.bc.security.jwt;

import ru.tdd.bc.security.dto.UserDto;

import javax.crypto.SecretKey;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * Сервис для работы с jwt токенами
 */
public interface JwtService {

    UserDto parseToken(String token, String secretKey);

    boolean validateToken(String token, String secretKey);

    SecretKey getSecret(String secretKey);

    String generateToken(UserDto user, String secretKey);
}
